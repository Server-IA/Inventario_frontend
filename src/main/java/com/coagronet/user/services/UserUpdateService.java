package com.coagronet.user.services;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.custom.RecursoDuplicadoException;
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

    private final UserRepository userRepository;
    private final PersonaRepository personaRepository;
    private final EmpresaRolRepository empresaRolRepository;
    private final EmpresaTenantIdentifierResolver tenantResolver;
    private final EntityManager entityManager;

    @Transactional
    public void updateUserDetails(Long requestedId, UsuarioUpdateRequest request) {
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

        // Validaciones de unicidad
        if (!user.getUsername().equals(request.username()) && userRepository.existsByUsername(request.username())) {
            throw new RecursoDuplicadoException("El username ya se encuentra registrado.");
        }

        Persona persona = user.getPersona();
        if (!persona.getIdentificacion().equals(request.identificacion())
                && personaRepository.existsByIdentificacion(request.identificacion())) {
            throw new RecursoDuplicadoException("La identificación ya se encuentra registrada.");
        }

        // Actualización de Persona
        persona.setIdentificacion(request.identificacion());
        if (request.tipoIdentificacionId() != null) {
            persona.setTipoIdentificacion(entityManager.getReference(TipoIdentificacion.class, request.tipoIdentificacionId()));
        }
        persona.setNombre(request.nombre());
        persona.setApellido(request.apellido());
        persona.setEmailPersonal(request.emailPersonal());
        persona.setGenero(request.genero());
        persona.setFechaNacimiento(request.fechaNacimiento());
        persona.setDireccion(request.direccion());
        persona.setCelular(request.celular());
        persona.setEstrato(request.estrato());
        personaRepository.save(persona);

        // Actualización de User
        user.setUsername(request.username());

        if (request.rolPreferidoId() != null) {
            user.setPreferredRol(entityManager.getReference(Rol.class, request.rolPreferidoId()));
        } else {
            user.setPreferredRol(null);
        }

        if (request.empresaPreferidaId() != null) {
            user.setPreferredEmpresa(entityManager.getReference(Empresa.class, request.empresaPreferidaId()));
        } else {
            user.setPreferredEmpresa(null);
        }

        // Procesar asignaciones
        if (request.asignaciones() != null) {
            for (AsignacionUpdateRequest asigReq : request.asignaciones()) {
                if (asigReq.rolId().equals(1L) && !isSystemAdmin) {
                    throw new AccessDeniedException("No tiene permisos para asignar el rol de Administrador del Sistema.");
                }

                if (asigReq.usuarioRolId() != null) {
                    // Update
                    UsuarioRol existingUr = user.getRolesAsignados().stream()
                            .filter(ur -> ur.getId().equals(asigReq.usuarioRolId()))
                            .findFirst()
                            .orElseThrow(() -> new RecursoNoEncontradoException("UsuarioRol", asigReq.usuarioRolId()));

                    if (!isSystemAdmin && existingUr.getEmpresa() != null
                            && !existingUr.getEmpresa().getId().equals(currentEmpresaId)) {
                        throw new AccessDeniedException("No tiene permisos para modificar esta asignación.");
                    }

                    Long empId = existingUr.getTenantEmpresaId();

                    empresaRolRepository.findRolByEmpresaIdAndRolIdAndEstadoId(empId, asigReq.rolId(), 1L)
                            .orElseThrow(() -> new RecursoNoEncontradoException("Rol no activo en la empresa", asigReq.rolId()));

                    existingUr.setRol(entityManager.getReference(Rol.class, asigReq.rolId()));
                    existingUr.setEstado(entityManager.getReference(Estado.class, asigReq.estadoId()));
                    existingUr.setIniciaContratoEn(asigReq.fechaInicioContrato());
                    existingUr.setFinalizaContratoEn(asigReq.fechaFinContrato());
                } else {
                    // Create
                    Long empId = isSystemAdmin ? asigReq.empresaId() : currentEmpresaId;
                    if (empId == null) {
                        throw new IllegalArgumentException("Debe especificar empresaId para nuevas asignaciones.");
                    }

                    empresaRolRepository.findRolByEmpresaIdAndRolIdAndEstadoId(empId, asigReq.rolId(), 1L)
                            .orElseThrow(() -> new RecursoNoEncontradoException("Rol no activo en la empresa", asigReq.rolId()));

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

        userRepository.save(user);
    }
}
