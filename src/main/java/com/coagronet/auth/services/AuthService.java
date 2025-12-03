package com.coagronet.auth.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coagronet.auth.dto.ApiResponse;
import com.coagronet.auth.dto.ChangePasswordRequestDTO;
import com.coagronet.auth.dto.EmpresaRolDTO;
import com.coagronet.auth.dto.ForgotPasswordRequestDTO;
import com.coagronet.auth.dto.LoginRequestDTO;
import com.coagronet.auth.dto.RegisterRequestDTO;
import com.coagronet.auth.dto.ResetPasswordRequestDTO;
import com.coagronet.auth.dto.SwitchContextRequestDTO;
import com.coagronet.auth.props.AuthProperties;
import com.coagronet.email.services.EmailVerificationService;
import com.coagronet.exceptionHandler.UserRoleForbiddenException;
import com.coagronet.infrastructure.security.JwtUtil;
import com.coagronet.rol.Rol;
import com.coagronet.rol.repositories.RolRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.user.services.UserRegistrationService;
import com.coagronet.usuarioEstado.UsuarioEstado;
import com.coagronet.usuariorol.UsuarioRol;
import com.coagronet.usuariorol.repositories.UsuarioRolRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@Transactional // ensures atomicity of operations
@RequiredArgsConstructor
public class AuthService {

	private final PasswordEncoder encoder;

	private final RolRepository rolRepository;

	private final JwtUtil jwt;

	private final UserRegistrationService registrationService;

	private final EmailVerificationService emailService;

	private final UserRepository userRepo;

	private final AuthenticationManager authManager;

	private final UsuarioRolRepository userRoleRepo;

	private final AuthProperties props; // e.g. defaultRole, etc.

	/* ================= REGISTRATION ================= */
	@Transactional
	public ApiResponse register(@Valid RegisterRequestDTO dto) {

		/* 1?? Does the user already exist? ---------------------------------- */
		User existing = userRepo.findByUsername(dto.getUsername()).orElse(null);

		if (existing != null) {

			/* 1a. Still pending verification ? 409 Conflict + resend email */
			if (existing.getUsuarioEstado() == UsuarioEstado.PENDIENTE_VERIFICACION) {

				// resend: generate (or reuse) token and send email again
				String token = emailService.createVerificationToken(existing.getUsername());
				emailService.sendVerificationEmail(existing.getUsername(), token);

				throw new ResponseStatusException(HttpStatus.CONFLICT,
						"El correo electrónico ya está registrado, pero no verificado. Se ha reenviado el enlace de verificación.");
			}

			/* 1b. Already active/in use ? 400 Bad Request */
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está en uso.");
		}

		/* 2?? Create a new user ---------------------------------------------- */
		User user = new User();
		user.setUsername(dto.getUsername());
		user.setPassword(encoder.encode(dto.getPassword()));
		user.setUsuarioEstado(UsuarioEstado.PENDIENTE_VERIFICACION);

		Rol role = rolRepository.findByNombre(props.getDefaultRole())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
		user.setRoles(Set.of(role));

		/* 3?? Register and send email (listener) ----------------------------- */
		registrationService.registerUser(user);

		/* 4?? Success ? 201 Created ------------------------------------------ */
		return new ApiResponse(true, "Correo electrónico de verificación enviado a " + user.getUsername());
	}

	// LOGIN
	public Map<String, Object> login(@Valid LoginRequestDTO dto) {
		Authentication auth = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
		User user = (User) auth.getPrincipal();

		List<UsuarioRol> usuarioRols = userRoleRepo.findByUserOrderByUserId(user);
		if (usuarioRols.isEmpty())
			throw new UserRoleForbiddenException("El usuario no tiene asignado ningún rol dentro de una empresa.");

		UsuarioRol current = resolveInitialContext(user, usuarioRols);

		if (current.getEmpresa() == null) {

			String token = jwt.generateToken(user, current.getRol().getId(), user.getUsuarioEstado().getId());

			return Map.of("token", token, "rolId", current.getRol().getId(), "estado",
					user.getUsuarioEstado().getId());

		}
		String token = jwt.generateToken(user, current.getEmpresa().getId(), current.getRol().getId(),
				user.getUsuarioEstado().getId());

		var nombrePersona = user.getPersona().getNombre() + " " + user.getPersona().getApellido();

		List<EmpresaRolDTO> rolesByCompany = usuarioRols.stream().map(ur -> new EmpresaRolDTO(ur.getEmpresa().getId(),
				ur.getEmpresa().getNombre(), ur.getRol().getId(), ur.getRol().getNombre())).toList();
		return Map.of("token", token, "empresaId", current.getEmpresa().getId(), "rolId", current.getRol().getId(),
				"rolesByCompany", rolesByCompany, "estado", user.getUsuarioEstado().getId(), "nombrePersona",
				nombrePersona);

	}

	// SWITCH CONTEXT
	public Map<String, Object> switchContext(@Valid SwitchContextRequestDTO dto, String username) {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		userRoleRepo.findByUserAndEmpresaIdAndRolId(user, dto.empresaId(), dto.rolId())
				.orElseThrow(() -> new UserRoleForbiddenException("Role/company not assigned to user"));

		String token = jwt.generateToken(user, dto.empresaId(), dto.rolId(), user.getUsuarioEstado().getId());
		if (Boolean.TRUE.equals(dto.rememberAsDefault())) {
			user.setPreferredEmpresaId(dto.empresaId());
			user.setPreferredRolId(dto.rolId());
			userRepo.save(user);
		}

		var nombrePersona = user.getPersona().getNombre() + " " + user.getPersona().getApellido();

		return Map.of("token", token, "empresaId", dto.empresaId(), "rolId", dto.rolId(), "estado",
				user.getUsuarioEstado().getId(), "nombrePersona", nombrePersona);
	}

	/* ================= Estrategia para el contexto inicial ================= */
	private UsuarioRol resolveInitialContext(User user, List<UsuarioRol> usuarioRols) {
		// 1) Si hay preferido en User, ?salo si existe a?n
		if (user.getPreferredEmpresaId() != null && user.getPreferredRolId() != null) {
			Optional<UsuarioRol> preferred = usuarioRols.stream()
					.filter(ur -> ur.getEmpresa().getId().equals(user.getPreferredEmpresaId())
							&& ur.getRol().getId().equals(user.getPreferredRolId()))
					.findFirst();
			if (preferred.isPresent())
				return preferred.get();
		}
		// 2) Si solo tiene uno, ese
		if (usuarioRols.size() == 1)
			return usuarioRols.get(0);
		// 3) Fallback: el primero (o el de menor id, o por fecha de creaci?n)
		return usuarioRols.get(0);
	}

	// CHANGE PASSWORD
	public ApiResponse changePassword(@Valid ChangePasswordRequestDTO dto) {
		User user = getCurrentUser();
		if (!encoder.matches(dto.getOldPassword(), user.getPassword()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");

		user.setPassword(encoder.encode(dto.getNewPassword()));
		user.incrementTokenVersion(); // <<<<<< revocar todos los JWT previos
		userRepo.save(user);

		return new ApiResponse(true, "Password changed successfully");
	}

	public ApiResponse forgotPassword(@Valid ForgotPasswordRequestDTO dto) {
		userRepo.findByUsername(dto.getEmail()).ifPresent(u -> {
			var token = emailService.createResetPasswordToken(u.getUsername());
			emailService.sendResetPasswordEmail(u.getUsername(), token);
		});
		return new ApiResponse(true, "If the email exists, you will receive a message shortly.");
	}

	// RESET PASSWORD
	public ApiResponse resetPassword(@Valid ResetPasswordRequestDTO dto) {
		String username = emailService.consumeResetPasswordToken(dto.getToken());
		if (username == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset link");

		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		user.setPassword(encoder.encode(dto.getNewPassword()));
		user.incrementTokenVersion(); // <<<<<< revocar todos los JWT previos
		userRepo.save(user);

		return new ApiResponse(true, "Password reset successfully");
	}

	/* ================= ACCOUNT VERIFICATION ================= */
	public ApiResponse verifyUser(String token) {
		// Dejamos que RegistrationService active y consuma el token VERIFY
		boolean ok = registrationService.activateUser(token);
		return ok ? new ApiResponse(true, "User activated successfully")
				: new ApiResponse(false, "Invalid verification link");
	}

	/* ================= LOGOUT (stateless) ================= */
	public void logout(HttpServletRequest req, HttpServletResponse res) {
		HttpSession session = req.getSession(false);
		if (session != null)
			session.invalidate();
		res.setStatus(HttpServletResponse.SC_OK);
	}

	/* ================= UTILITIES ================= */
	public Set<String> getCurrentUserRoles() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof UserDetails ud))
			return Set.of();
		return ud.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(java.util.stream.Collectors.toSet());
	}

	private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth
				.getPrincipal() instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");

		return userRepo.findByUsername(auth.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}

}
