package com.coagronet.auth;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.auth.dto.ApiResponse;
import com.coagronet.auth.dto.ChangePasswordRequestDTO;
import com.coagronet.auth.dto.ForgotPasswordRequestDTO;
import com.coagronet.auth.dto.LoginRequestDTO;
import com.coagronet.auth.dto.RegisterRequestDTO;
import com.coagronet.auth.dto.ResetPasswordRequestDTO;
import com.coagronet.auth.dto.SelectRoleRequestDTO;
import com.coagronet.auth.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequestDTO dto) {
		return ResponseEntity.ok(authService.register(dto));
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> preLogin(@Valid @RequestBody LoginRequestDTO dto) {
		return ResponseEntity.ok(authService.preLogin(dto));
	}

	@PostMapping("/login/seleccion")
	public ResponseEntity<Map<String, Object>> selectRole(@Valid @RequestBody SelectRoleRequestDTO dto) {
		return ResponseEntity.ok(authService.selectRole(dto));
	}

	@PostMapping("/change-password")
	public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequestDTO dto) {
		return ResponseEntity.ok(authService.changePassword(dto));
	}

	@GetMapping("/verify")
	public ResponseEntity<ApiResponse> verify(@RequestParam String token) {
		return ResponseEntity.ok(authService.verifyUser(token));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
		return ResponseEntity.ok(authService.forgotPassword(dto));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
		return ResponseEntity.ok(authService.resetPassword(dto));
	}

	@PostMapping("/logout")
	public void logout(HttpServletRequest req, HttpServletResponse res) {
		authService.logout(req, res);
	}

	/** sólo utilitario de perfil logeado */
	@GetMapping("/roles")
	public Set<String> roles() {
		return authService.getCurrentUserRoles();
	}

	@GetMapping("/debug-login-dto")
	public ResponseEntity<List<String>> debugLoginDto() {
		Field[] fields = LoginRequestDTO.class.getDeclaredFields();
		List<String> fieldNames = new ArrayList<>();
		for (Field field : fields) {
			fieldNames.add(field.getName());
		}
		return ResponseEntity.ok(fieldNames);
	}

}
