/*=============================================================================
 Nombre del archivo : UserRegistrationService.java
 Descripcion        : Servicio de aplicación para el registro de usuarios y
                      asignación de roles empresariales.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-16 | 0.4.0   | JUAN JOSE CASTRO     | Implementación de generación|
 |            |         |                      | automática de contraseñas   |
 |            |         |                      | seguras para usuarios nuevos|
 |            |         |                      | en lugar de recibirla en la |
 |            |         |                      | petición. Publicación de    |
 |            |         |                      | eventos para envío de       |
 |            |         |                      | credenciales y roles.       |
 |            |         |                      | Asignación de estado usando |
 |            |         |                      | ID_ACTIVADO_CON_EMPRESA.    |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.inventario.user.services;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.List;

import com.inventario.empresa.Empresa;
import com.inventario.empresarol.repositories.EmpresaRolRepository;
import com.inventario.estado.Estado;
import com.inventario.exceptionHandler.custom.RecursoDuplicadoException;
import com.inventario.infrastructure.configuration.EmpresaTenantIdentifierResolver;
import com.inventario.persona.Persona;
import com.inventario.persona.repositories.PersonaRepository;
import com.inventario.rol.Rol;
import com.inventario.tipoIdentificacion.TipoIdentificacion;
import com.inventario.user.User;
import com.inventario.user.dtos.AsignacionRequest;
import com.inventario.user.dtos.UserRegistrationRequest;
import com.inventario.user.events.OnRegistrationCompleteEvent;
import com.inventario.user.repositories.UserRepository;
import com.inventario.auth.events.NewUserCredentialsEvent;
import com.inventario.auth.listeners.RoleActivatedEvent;
import com.inventario.utils.PasswordGenerator;
import com.inventario.usuarioEstado.UsuarioEstado;
import com.inventario.usuarioEstado.repositories.UsuarioEstadoRepository;
import com.inventario.usuariorol.UsuarioRol;
import com.inventario.verificationToken.TokenPurpose;
import com.inventario.verificationToken.repositories.VerificationTokenRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private final ApplicationEventPublisher publisher;

    private final UsuarioEstadoRepository usuarioEstadoRepository;

    private final PersonaRepository personaRepository;

    private final EmpresaRolRepository empresaRolRepository;

    private final EmpresaTenantIdentifierResolver tenantResolver;

    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    private static final Long ESTADO_ACTIVO_ID = 1L;

    @Transactional
    public void registerUser(User user, String acceptLanguage) {
        userRepository.save(user);
        publisher.publishEvent(new OnRegistrationCompleteEvent(user, acceptLanguage));
    }

    @Transactional
    public boolean activateUser(String token) {
        var tokenOptional = verificationTokenRepository.findByTokenAndPurpose(token, TokenPurpose.VERIFY);
        if (tokenOptional.isEmpty())
            return false;

        var vt = tokenOptional.get();
        if (vt.isExpired()) {
            verificationTokenRepository.delete(vt); // opcional, para limpieza
            return false;
        }

        var user = userRepository.findByUsername(vt.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + vt.getEmail()));

        user.setUsuarioEstado(usuarioEstadoRepository.getReferenceById(UsuarioEstado.ID_ACTIVADO_SIN_INFO));
        userRepository.save(user);

        // ? MUY IMPORTANTE: invalidar el token tras activar
        verificationTokenRepository.delete(vt);

        return true;
    }

    @Transactional
    public Long registerOrUpdateUser(UserRegistrationRequest request) {
        validarFechasContrato(request);

        final Persona persona;

        Optional<Persona> personaExistente = personaRepository.findByIdentificacion(request.identificacion());

        if (personaExistente.isPresent()) {
            persona = personaExistente.get();

            persona.setTipoIdentificacion(
                    entityManager.getReference(TipoIdentificacion.class, request.tipoIdentificacionId()));
            persona.setNombre(request.nombre());
            persona.setApellido(request.apellido());
            persona.setEmailPersonal(request.emailPersonal());
            persona.setGenero(request.genero());
            persona.setFechaNacimiento(request.fechaNacimiento());
            persona.setDireccion(request.direccion());
            persona.setCelular(request.celular());
            persona.setEstrato(request.estrato());
            persona.setEstado(entityManager.getReference(Estado.class, ESTADO_ACTIVO_ID));
            personaRepository.save(persona);
        } else {
            persona = personaRepository.save(crearNuevaPersona(request));
        }

        boolean isNewUser = false;
        String tempPassword = null;

        User user = userRepository.findByPersonaId(persona.getId()).orElse(null);

        if (user == null) {
            isNewUser = true;
            tempPassword = PasswordGenerator.generateStrongPassword();
            user = crearNuevoUsuario(request, persona, tempPassword);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isSystemAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR_SISTEMA"));
        Long sessionEmpresaId = isSystemAdmin ? null : tenantResolver.resolveCurrentTenantIdentifier();

        boolean seAsignoPreferida = false;
        String acceptLanguage = LocaleContextHolder.getLocale().toLanguageTag();
        List<UsuarioRol> newlyAddedRoles = new ArrayList<>();

        for (AsignacionRequest asignacion : request.asignaciones()) {
            Long empresaAsignarId = isSystemAdmin ? asignacion.empresaId() : sessionEmpresaId;

            if (empresaAsignarId == null) {
                throw new RecursoDuplicadoException("No se pudo determinar la empresa.");
            }

            UsuarioRol usuarioRol = UsuarioRol.builder()
                    .empresa(entityManager.getReference(Empresa.class, empresaAsignarId))
                    .tenantEmpresaId(empresaAsignarId)
                    .rol(empresaRolRepository
                            .findRolByEmpresaIdAndRolIdAndEstadoId(empresaAsignarId, asignacion.rolId(),
                                    ESTADO_ACTIVO_ID)
                            .orElseThrow(() -> new RecursoDuplicadoException("Rol no activo")))
                    .estado(entityManager.getReference(Estado.class, ESTADO_ACTIVO_ID))
                    .iniciaContratoEn(asignacion.iniciaContratoEn())
                    .finalizaContratoEn(asignacion.finalizaContratoEn())
                    .build();

            user.addUsuarioRol(usuarioRol);
            newlyAddedRoles.add(usuarioRol);

            if (Boolean.TRUE.equals(asignacion.esPreferida())) {
                user.setPreferredEmpresa(entityManager.getReference(Empresa.class, empresaAsignarId));
                user.setPreferredRol(entityManager.getReference(Rol.class, asignacion.rolId()));
                seAsignoPreferida = true;
            }
        }

        if (!seAsignoPreferida && user.getPreferredEmpresa() == null && !request.asignaciones().isEmpty()) {
            AsignacionRequest primera = request.asignaciones().get(0);
            Long empresaId = isSystemAdmin ? primera.empresaId() : sessionEmpresaId;
            user.setPreferredEmpresa(entityManager.getReference(Empresa.class, empresaId));
            user.setPreferredRol(entityManager.getReference(Rol.class, primera.rolId()));
        }

        userRepository.save(user);

        boolean firstAssignment = true;
        for (UsuarioRol ur : newlyAddedRoles) {
            if (isNewUser && firstAssignment) {
                publisher.publishEvent(new NewUserCredentialsEvent(ur.getId(), tempPassword, acceptLanguage));
                firstAssignment = false;
            } else {
                publisher.publishEvent(new RoleActivatedEvent(ur.getId(), acceptLanguage));
            }
        }

        return user.getId();
    }

    private void validarFechasContrato(UserRegistrationRequest request) {
        if (request.asignaciones() == null || request.asignaciones().isEmpty()) {
            throw new RecursoDuplicadoException("Debe agregar al menos una asignación.");
        }
        for (AsignacionRequest asignacion : request.asignaciones()) {
            if (asignacion.finalizaContratoEn() != null &&
                    asignacion.finalizaContratoEn().isBefore(asignacion.iniciaContratoEn())) {
                throw new RecursoDuplicadoException(
                        "La fecha de fin de contrato no puede ser menor a la fecha de inicio.");
            }
        }
    }

    private Persona crearNuevaPersona(UserRegistrationRequest request) {
        return Persona.builder()
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
                .estado(entityManager.getReference(Estado.class, ESTADO_ACTIVO_ID))
                .build();
    }

    private User crearNuevoUsuario(UserRegistrationRequest request, Persona persona, String password) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RecursoDuplicadoException("El username ya se encuentra registrado.");
        }
        return User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(password))
                .persona(persona)
                .usuarioEstado(entityManager.getReference(UsuarioEstado.class, UsuarioEstado.ID_ACTIVADO_CON_EMPRESA))
                .build();
    }

}
