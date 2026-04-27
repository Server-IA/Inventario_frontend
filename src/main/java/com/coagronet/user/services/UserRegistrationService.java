package com.coagronet.user.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.custom.RecursoDuplicadoException;
import com.coagronet.infrastructure.configuration.EmpresaTenantIdentifierResolver;
import com.coagronet.persona.Persona;
import com.coagronet.persona.repositories.PersonaRepository;
import com.coagronet.rol.Rol;
import com.coagronet.tipoIdentificacion.TipoIdentificacion;
import com.coagronet.user.User;
import com.coagronet.user.dtos.AsignacionRequest;
import com.coagronet.user.dtos.UserRegistrationRequest;
import com.coagronet.user.events.OnRegistrationCompleteEvent;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.usuarioEstado.UsuarioEstado;
import com.coagronet.usuarioEstado.repositories.UsuarioEstadoRepository;
import com.coagronet.usuariorol.UsuarioRol;
import com.coagronet.verificationToken.TokenPurpose;
import com.coagronet.verificationToken.repositories.VerificationTokenRepository;

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
	private static final Long USUARIO_ESTADO_ACTIVO_ID = 4L;

	@Transactional
	public void registerUser(User user) {
		userRepository.save(user);
		publisher.publishEvent(new OnRegistrationCompleteEvent(user));
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
		// 1. Validar fechas de contrato
		validarFechasContrato(request);

		// 2. Determinar si la persona ya existe. Si no existe, se crea y persiste
		// inmediatamente.
		// La variable resultante es 'effectively final'.
		final Persona persona = personaRepository.findByIdentificacion(request.identificacion())
				.orElseGet(() -> personaRepository.save(crearNuevaPersona(request)));

		// 3. Determinar si ya tiene un User asociado.
		// Ahora es seguro usar 'persona' dentro de la lambda.
		User user = userRepository.findByPersonaId(persona.getId())
				.orElseGet(() -> crearNuevoUsuario(request, persona));

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// 4. Asignar Roles y Empresas según el contexto (Admin vs Empresa)
		boolean isSystemAdmin = auth != null && auth.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(role -> role.equals("ROLE_ADMINISTRADOR_SISTEMA"));

		Long sessionEmpresaId = isSystemAdmin ? null : tenantResolver.resolveCurrentTenantIdentifier();

		for (AsignacionRequest asignacion : request.asignaciones()) {
			Long empresaAsignarId = isSystemAdmin ? asignacion.empresaId() : sessionEmpresaId;

			// Validación de integridad
			if (empresaAsignarId == null) {
				throw new RecursoDuplicadoException("No se pudo determinar la empresa para la asignación.");
			}

			UsuarioRol usuarioRol = UsuarioRol.builder()
					.empresa(entityManager.getReference(Empresa.class, empresaAsignarId))
					.tenantEmpresaId(empresaAsignarId)
					.rol(empresaRolRepository
							.findRolByEmpresaIdAndRolIdAndEstadoId(empresaAsignarId, asignacion.rolId(),
									ESTADO_ACTIVO_ID)
							.orElseThrow(() -> new RecursoDuplicadoException(
									"El rol con ID " + asignacion.rolId() + " no está activo para la empresa")))
					.estado(entityManager.getReference(Estado.class, ESTADO_ACTIVO_ID))
					.iniciaContratoEn(asignacion.iniciaContratoEn())
					.finalizaContratoEn(asignacion.finalizaContratoEn())
					.build();

			user.addUsuarioRol(usuarioRol);
		}

		// 5. Preferencias: Marcar la primera asignación como preferida por defecto
		if (user.getPreferredEmpresaId() == null && !request.asignaciones().isEmpty()) {
			Long prefferedEmpresa = isSystemAdmin ? request.asignaciones().get(0).empresaId() : sessionEmpresaId;
			user.setPreferredEmpresaId(prefferedEmpresa);
			user.setPreferredRolId(request.asignaciones().get(0).rolId());
		}

		userRepository.save(user);
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
				.estado(entityManager.getReference(Estado.class, ESTADO_ACTIVO_ID))
				.build();
	}

	private User crearNuevoUsuario(UserRegistrationRequest request, Persona persona) {
		if (userRepository.existsByUsername(request.username())) {
			throw new RecursoDuplicadoException("El username ya se encuentra registrado.");
		}
		return User.builder()
				.username(request.username())
				.password(passwordEncoder.encode(request.password()))
				.persona(persona)
				.usuarioEstado(entityManager.getReference(UsuarioEstado.class, USUARIO_ESTADO_ACTIVO_ID))
				.build();
	}

}
