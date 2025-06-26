package com.coagronet.infrastructure.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.coagronet.email.services.EmailVerificationService;
import com.coagronet.exceptionHandler.UserRoleForbiddenException;
import com.coagronet.role.Role;
import com.coagronet.role.repositories.RoleRepository;
import com.coagronet.user.User;
import com.coagronet.user.dtos.ApiResponse;
import com.coagronet.user.dtos.ChangePasswordRequestDTO;
import com.coagronet.user.dtos.ForgotPasswordRequestDTO;
import com.coagronet.user.dtos.LoginRequestDTO;
import com.coagronet.user.dtos.RegisterRequestDTO;
import com.coagronet.user.dtos.ResetPasswordRequestDTO;
import com.coagronet.user.dtos.SelectRoleRequestDTO;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.user.services.UserRegistrationService;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;
import com.coagronet.usuarioEstado.UsuarioEstado;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	@Value("${jwt.default_role}")
	private String defaultRole;

	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;
	private final JwtUtil jwtUtil;
	private final UserRegistrationService userRegistrationService;
	private final EmailVerificationService emailVerificationService;
	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final UserRoleRepository userRoleRepository;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
		if (userRepository.existsByUsername(registerRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Email ya está en uso."));
		}

		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setUsuarioEstado(UsuarioEstado.ACTIVADO_SIN_INFO);

		Role userRole = roleRepository.findByName(defaultRole)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
		user.setRoles(Set.of(userRole));

		userRegistrationService.registerUser(user);

		String token = emailVerificationService.createVerificationToken(user.getUsername());
		emailVerificationService.sendVerificationEmail(user.getUsername(), token);

		return ResponseEntity.ok(new ApiResponse(true, "Correo de verificación enviado a " + user.getUsername()));
	}

	@PostMapping("/login")
	public ResponseEntity<?> preLogin(@Valid @RequestBody LoginRequestDTO request) {
		// 1. Validar usuario y contraseña (sin autenticar todavía la sesión)
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		User authenticatedUser = (User) authentication.getPrincipal();

		// 2. Obtener roles/empresas del usuario
		List<UserRole> userRoles = userRoleRepository.findByUser(authenticatedUser);
		List<Map<String, Object>> rolesPorEmpresa = userRoles.stream().map(userRole -> {
			Map<String, Object> map = new HashMap<>();
			map.put("empresaId", userRole.getEmpresa().getId());
			map.put("empresaNombre", userRole.getEmpresa().getNombre());
			map.put("rolId", userRole.getRole().getId());
			map.put("rolNombre", roleRepository.findById(userRole.getRole().getId()).map(Role::getName).orElse(null));
			return map;
		}).collect(Collectors.toList());

		// 3. Responder con los roles/empresas disponibles
		Map<String, Object> response = new HashMap<>();
		response.put("rolesPorEmpresa", rolesPorEmpresa);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/login/seleccion")
	public ResponseEntity<?> seleccionarRol(@RequestBody SelectRoleRequestDTO request) {
		// 1. Validar que el usuario tiene ese rol/empresa asignado
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

		userRoleRepository.findByUserAndEmpresaIdAndRoleId(
				user, request.getEmpresaId(), request.getRolId())
				.orElseThrow(() -> new UserRoleForbiddenException(
						"No tienes asignado el rol o empresa seleccionada. Por favor, verifica tus permisos."));

		// 2. Generar el token JWT para ese contexto
		String token = jwtUtil.generateToken(user.getUsername(), request.getEmpresaId(), request.getRolId());

		Map<String, Object> response = new HashMap<>();
		response.put("token", token);
		response.put("empresaId", request.getEmpresaId());
		response.put("rolId", request.getRolId());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/change-password")
	public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequestDTO dto) {
		String username = getUserName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

		if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Contraseña antigua incorrecta"));
		}

		user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		userRepository.save(user);

		return ResponseEntity.ok(new ApiResponse(true, "Contraseña cambiada exitosamente"));
	}

	@GetMapping("/verify")
	public ResponseEntity<ApiResponse> verifyUser(@RequestParam String token) {
		boolean isVerified = userRegistrationService.activateUser(token);
		if (isVerified) {
			return ResponseEntity.ok(new ApiResponse(true, "Usuario activado correctamente"));
		}
		return ResponseEntity.badRequest().body(new ApiResponse(false, "Enlace de verificación inválido"));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
		User user = userRepository.findByUsername(dto.getEmail())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

		String token = emailVerificationService.createVerificationToken(user.getUsername());
		emailVerificationService.sendResetPasswordEmail(user.getUsername(), token);

		return ResponseEntity.ok(new ApiResponse(true, "Correo de recuperación enviado"));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
		String username = emailVerificationService.getEmailAndInvalidateToken(dto.getToken());
		if (username == null) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Enlace de recuperación inválido o expirado"));
		}

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

		user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		userRepository.save(user);

		return ResponseEntity.ok(new ApiResponse(true, "Contraseña restablecida exitosamente"));
	}

	@PostMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@GetMapping("/roles")
	public Set<String> getUserRoles() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
			return userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.toSet());
		}
		return Set.of();
	}

	@GetMapping("/home")
	public String home() {
		return "Welcome to the home page! UserName: " + getUserName();
	}

	private String getUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken || authentication == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
		}
		return authentication.getName();
	}
}
