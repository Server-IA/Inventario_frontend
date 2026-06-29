/*=============================================================================
 Nombre del archivo : UserUpdateService.java
 Descripcion        : Servicio para la actualización de detalles de usuario, 
                      información personal y asignación de roles.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-24 | 0.4.0   | JUAN JOSE CASTRO     | Refactorización de las      |
 |            |         |                      | validaciones para acumular  |
 |            |         |                      | errores y lanzar un único   |
 |            |         |                      | BadRequestException.        |
 |            |         |                      | Adición de inactivación de  |
 |            |         |                      | roles no incluidos en el    |
 |            |         |                      | payload y soporte para la   |
 |            |         |                      | creación de nueva Persona.  |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.user.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.exceptionHandler.custom.RecursoNoEncontradoException;
import com.coagronet.infrastructure.configuration.EmpresaTenantIdentifierResolver;
import com.coagronet.persona.Persona;
import com.coagronet.persona.repositories.PersonaRepository;
import com.coagronet.rol.Rol;
import com.coagronet.tipoIdentificacion.TipoIdentificacion;
import com.coagronet.user.User;
import com.coagronet.user.dtos.AsignacionUpdateRequest;
import com.coagronet.user.dtos.UsuarioUpdateRequest;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.usuariorol.UsuarioRol;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserUpdateService {

    private static final Long ESTADO_INACTIVO_ID = 2L;

    private final UserRepository userRepository;
    private final PersonaRepository personaRepository;
    private final EmpresaRolRepository empresaRolRepository;
    private final EmpresaTenantIdentifierResolver tenantResolver;
    private final EntityManager entityManager;

    @Transactional
    public void updateUserDetails(Long requestedId, UsuarioUpdateRequest request) {
        Map<String, String> errors = new HashMap<>();
        User user = userRepository.findById(requestedId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", requestedId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isSystemAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR_SISTEMA"));

        Long currentEmpresaId = isSystemAdmin ? null : tenantResolver.resolveCurrentTenantIdentifier();

        boolean hasAccess = isSystemAdmin || user.getRolesAsignados().stream()
                .anyMatch(ur -> ur.getEmpresa() != null && ur.getEmpresa().getId().equals(currentEmpresaId));

        if (!hasAccess) {
            throw new AccessDeniedException("No tiene permisos para modificar este usuario.");
        }

        // --- 1. FASE DE VALIDACIÓN ---

        // Validar Username
        if (!user.getUsername().equals(request.username()) && userRepository.existsByUsername(request.username())) {
            errors.put("username", "El username ya se encuentra registrado.");
        }

        // Validar Identificación
        Persona persona = user.getPersona();
        boolean isNewPersona = (persona == null);

        if (isNewPersona) {
            if (personaRepository.existsByIdentificacion(request.identificacion())) {
                errors.put("identificacion", "La identificación ya se encuentra registrada.");
            }
            if (request.tipoIdentificacionId() == null) {
                errors.put("tipoIdentificacionId", "El tipo de identificación es requerido.");
            }
        } else {
            if (!persona.getIdentificacion().equals(request.identificacion())
                    && personaRepository.existsByIdentificacion(request.identificacion())) {
                errors.put("identificacion", "La identificación ya se encuentra registrada.");
            }
        }

        // Validar Asignaciones
        if (request.asignaciones() != null) {
            for (int i = 0; i < request.asignaciones().size(); i++) {
                AsignacionUpdateRequest asigReq = request.asignaciones().get(i);
                String prefix = "asignaciones[" + i + "].";

                // 1. Validar permisos de rol de sistema
                if (asigReq.rolId().equals(1L) && !isSystemAdmin) {
                    errors.put(prefix + "rolId", "No tiene permisos para asignar el rol de Administrador del Sistema.");
                }

                if (asigReq.usuarioRolId() != null) { // Es una actualización
                    UsuarioRol existingUr = user.getRolesAsignados().stream()
                            .filter(ur -> ur.getId().equals(asigReq.usuarioRolId()))
                            .findFirst()
                            .orElse(null);

                    if (existingUr == null) {
                        errors.put(prefix + "usuarioRolId", "La asignación especificada no existe en este usuario.");
                    } else {
                        if (!isSystemAdmin && existingUr.getEmpresa() != null
                                && !existingUr.getEmpresa().getId().equals(currentEmpresaId)) {
                            errors.put(prefix + "usuarioRolId", "No tiene permisos para modificar esta asignación.");
                        } else {
                            // Validar que el rol esté activo en la empresa
                            Long empId = existingUr.getTenantEmpresaId();
                            boolean rolActivo = empresaRolRepository
                                    .findRolByEmpresaIdAndRolIdAndEstadoId(empId, asigReq.rolId(), 1L).isPresent();
                            if (!rolActivo) {
                                errors.put(prefix + "rolId", "Rol no activo en la empresa asociada.");
                            }
                        }
                    }
                } else { // Es una creación
                    Long empId = isSystemAdmin ? asigReq.empresaId() : currentEmpresaId;
                    if (empId == null) {
                        errors.put(prefix + "empresaId", "Debe especificar la empresa para nuevas asignaciones.");
                    } else {
                        // Validar que el rol esté activo en la empresa
                        boolean rolActivo = empresaRolRepository
                                .findRolByEmpresaIdAndRolIdAndEstadoId(empId, asigReq.rolId(), 1L).isPresent();
                        if (!rolActivo) {
                            errors.put(prefix + "rolId", "Rol no activo en la empresa asociada.");
                        }
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("La solicitud contiene datos inválidos o acciones no permitidas.", errors);
        }

        // --- 2. FASE DE MUTACIÓN Y PERSISTENCIA ---

        if (isNewPersona) {
            persona = Persona.builder()
                    .tipoIdentificacion(
                            entityManager.getReference(TipoIdentificacion.class, request.tipoIdentificacionId()))
                    .identificacion(request.identificacion())
                    .nombre(request.nombre())
                    .apellido(request.apellido())
                    .emailPersonal(request.emailPersonal())
                    .genero(request.genero())
                    .fechaNacimiento(request.fechaNacimiento())
                    .direccion(request.direccion())
                    .celular(request.celular())
                    .estrato(request.estrato())
                    .estado(entityManager.getReference(Estado.class, 1L))
                    .build();
        } else {
            persona.setIdentificacion(request.identificacion());
            if (request.tipoIdentificacionId() != null) {
                persona.setTipoIdentificacion(
                        entityManager.getReference(TipoIdentificacion.class, request.tipoIdentificacionId()));
            }
            persona.setNombre(request.nombre());
            persona.setApellido(request.apellido());
            persona.setEmailPersonal(request.emailPersonal());
            persona.setGenero(request.genero());
            persona.setFechaNacimiento(request.fechaNacimiento());
            persona.setDireccion(request.direccion());
            persona.setCelular(request.celular());
            persona.setEstrato(request.estrato());
        }

        personaRepository.save(persona);
        user.setPersona(persona);

        user.setUsername(request.username());
        user.setPreferredRol(
                request.rolPreferidoId() != null ? entityManager.getReference(Rol.class, request.rolPreferidoId())
                        : null);
        user.setPreferredEmpresa(request.empresaPreferidaId() != null
                ? entityManager.getReference(Empresa.class, request.empresaPreferidaId())
                : null);

        // Procesar asignaciones
        if (request.asignaciones() != null) {
            for (AsignacionUpdateRequest asigReq : request.asignaciones()) {
                if (asigReq.usuarioRolId() != null) {
                    // Update
                    UsuarioRol existingUr = user.getRolesAsignados().stream()
                            .filter(ur -> ur.getId().equals(asigReq.usuarioRolId()))
                            .findFirst().get();

                    existingUr.setRol(entityManager.getReference(Rol.class, asigReq.rolId()));
                    existingUr.setEstado(entityManager.getReference(Estado.class, asigReq.estadoId()));
                    existingUr.setIniciaContratoEn(asigReq.fechaInicioContrato());
                    existingUr.setFinalizaContratoEn(asigReq.fechaFinContrato());
                } else {
                    // Create
                    Long empId = isSystemAdmin ? asigReq.empresaId() : currentEmpresaId;

                    UsuarioRol newUr = UsuarioRol.builder()
                            .empresa(entityManager.getReference(Empresa.class, empId))
                            .tenantEmpresaId(empId)
                            .rol(entityManager.getReference(Rol.class, asigReq.rolId()))
                            .estado(entityManager.getReference(Estado.class, asigReq.estadoId()))
                            .iniciaContratoEn(asigReq.fechaInicioContrato())
                            .finalizaContratoEn(asigReq.fechaFinContrato())
                            .build();

                    user.addUsuarioRol(newUr);
                }
            }
        }

        // Inactivar asignaciones no incluidas en el payload
        Set<Long> asignacionesRecibidas = request.asignaciones() != null
                ? request.asignaciones().stream()
                        .filter(a -> a.usuarioRolId() != null)
                        .map(AsignacionUpdateRequest::usuarioRolId)
                        .collect(Collectors.toSet())
                : Collections.emptySet();

        for (UsuarioRol ur : user.getRolesAsignados()) {
            if (!asignacionesRecibidas.contains(ur.getId())) {
                if (isSystemAdmin || (ur.getEmpresa() != null && ur.getEmpresa().getId().equals(currentEmpresaId))) {
                    ur.setEstado(entityManager.getReference(Estado.class, ESTADO_INACTIVO_ID));
                }
            }
        }

        userRepository.save(user);
    }
}