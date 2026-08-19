package com.inventario.auth;

import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.auth.dto.ApiResponse;
import com.inventario.auth.dto.ChangePasswordRequestDTO;
import com.inventario.auth.dto.ForgotPasswordRequestDTO;
import com.inventario.auth.dto.InitialPasswordChangeRequestDTO;
import com.inventario.auth.dto.LoginRequestDTO;
import com.inventario.auth.dto.RegisterForCurrentEmpresaRequestDTO;
import com.inventario.auth.dto.RegisterRequestDTO;
import com.inventario.auth.dto.ResetPasswordRequestDTO;
import com.inventario.auth.dto.SwitchContextRequestDTO;
import com.inventario.auth.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequestDTO dto,
	    @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
	    HttpServletRequest httpRequest) {
	String languageHeader = acceptLanguage;
	if (languageHeader == null || languageHeader.isBlank()) {
	    languageHeader = httpRequest.getHeader("Accept-Language");
	}
	if ((languageHeader == null || languageHeader.isBlank()) && httpRequest.getLocale() != null) {
	    languageHeader = httpRequest.getLocale().toLanguageTag();
	}
	return ResponseEntity.ok(authService.register(dto, languageHeader));
    }

    @PostMapping("/empresa/usuario-roles")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterForCurrentEmpresaRequestDTO dto,
	    HttpServletRequest httpRequest) {
	return ResponseEntity.ok(authService.registerForCurrentEmpresa(dto, httpRequest));
    }

    @PostMapping("/v2/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequestDTO dto) {
	return authService.login(dto);
    }

    @PostMapping("/switch-context")
    public Map<String, Object> switchContext(@Valid @RequestBody SwitchContextRequestDTO dto,
	    Authentication authentication) {
	String username = authentication.getName();
	return authService.switchContext(dto, username);
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
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto,
	    @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {
	return ResponseEntity.ok(authService.forgotPassword(dto, acceptLanguage));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
	return ResponseEntity.ok(authService.resetPassword(dto));
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest req, HttpServletResponse res) {
	authService.logout(req, res);
    }

    /** s?lo utilitario de perfil logeado */
    @GetMapping("/roles")
    public Set<String> roles() {
	return authService.getCurrentUserRoles();
    }

    @PostMapping("/change-password-initial")
    public ResponseEntity<ApiResponse> changePasswordInitial(@Valid @RequestBody InitialPasswordChangeRequestDTO dto) {
	return ResponseEntity.ok(authService.changePasswordInitial(dto));
    }

}
