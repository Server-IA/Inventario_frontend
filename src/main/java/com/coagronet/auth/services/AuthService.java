package com.coagronet.auth.services;

import java.util.List;
import java.util.Map;
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

import com.coagronet.auth.dto.*;
import com.coagronet.auth.props.AuthProperties;
import com.coagronet.email.services.EmailVerificationService;
import com.coagronet.exceptionHandler.UserRoleForbiddenException;
import com.coagronet.infrastructure.security.JwtUtil;
import com.coagronet.role.Role;
import com.coagronet.role.repositories.RoleRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.user.services.UserRegistrationService;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;
import com.coagronet.usuarioEstado.UsuarioEstado;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@Transactional // atomicidad en operaciones
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder encoder;
    private final RoleRepository roleRepo;
    private final JwtUtil jwt;
    private final UserRegistrationService registrationService;
    private final EmailVerificationService emailService;
    private final UserRepository userRepo;
    private final AuthenticationManager authManager;
    private final UserRoleRepository userRoleRepo;
    private final AuthProperties props; // defaultRole, etc.

    /* ================= REGISTRO ================= */

    public ApiResponse register(@Valid RegisterRequestDTO dto) {
        if (userRepo.existsByUsername(dto.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ya está en uso");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setUsuarioEstado(UsuarioEstado.ACTIVADO_SIN_INFO);

        Role role = roleRepo.findByName(props.getDefaultRole())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
        user.setRoles(Set.of(role));

        registrationService.registerUser(user);

        String token = emailService.createVerificationToken(user.getUsername());
        emailService.sendVerificationEmail(user.getUsername(), token);

        return new ApiResponse(true, "Correo de verificación enviado a " + user.getUsername());
    }

    /* ================= LOGIN – paso 1 ================= */

    public Map<String, Object> preLogin(@Valid LoginRequestDTO dto) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        User user = (User) auth.getPrincipal();

        List<UserRole> userRoles = userRoleRepo.findByUser(user);

        List<EmpresaRolDTO> rolesPorEmpresa = userRoles.stream()
                .map(ur -> new EmpresaRolDTO(
                        ur.getEmpresa().getId(),
                        ur.getEmpresa().getNombre(),
                        ur.getRole().getId(),
                        ur.getRole().getName()))
                .toList();

        return Map.of("rolesPorEmpresa", rolesPorEmpresa);

    }

    /* ================= LOGIN – paso 2 ================= */

    public Map<String, Object> selectRole(@Valid SelectRoleRequestDTO dto) {

        User user = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        userRoleRepo.findByUserAndEmpresaIdAndRoleId(user, dto.getEmpresaId(), dto.getRolId())
                .orElseThrow(() -> new UserRoleForbiddenException("Rol/empresa no asignados al usuario"));

        String token = jwt.generateToken(user.getUsername(), dto.getEmpresaId(), dto.getRolId());

        return Map.of(
                "token", token,
                "empresaId", dto.getEmpresaId(),
                "rolId", dto.getRolId());
    }

    /* ================= CAMBIO, OLVIDO y RESET de contraseña ================= */

    public ApiResponse changePassword(@Valid ChangePasswordRequestDTO dto) {
        User user = getCurrentUser();
        if (!encoder.matches(dto.getOldPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contraseña antigua incorrecta");

        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepo.save(user);

        return new ApiResponse(true, "Contraseña cambiada exitosamente");
    }

    public ApiResponse forgotPassword(@Valid ForgotPasswordRequestDTO dto) {
        User user = userRepo.findByUsername(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        String token = emailService.createVerificationToken(user.getUsername());
        emailService.sendResetPasswordEmail(user.getUsername(), token);

        return new ApiResponse(true, "Correo de recuperación enviado");
    }

    public ApiResponse resetPassword(@Valid ResetPasswordRequestDTO dto) {
        String username = emailService.getEmailAndInvalidateToken(dto.getToken());
        if (username == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enlace de recuperación inválido o expirado");

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepo.save(user);

        return new ApiResponse(true, "Contraseña restablecida exitosamente");
    }

    /* ================= ACTIVACIÓN ================= */

    public ApiResponse verifyUser(String token) {
        boolean ok = registrationService.activateUser(token);
        return ok ? new ApiResponse(true, "Usuario activado correctamente")
                : new ApiResponse(false, "Enlace de verificación inválido");
    }

    /* ================= LOGOUT (stateless) ================= */

    public void logout(HttpServletRequest req, HttpServletResponse res) {
        HttpSession session = req.getSession(false);
        if (session != null)
            session.invalidate();
        res.setStatus(HttpServletResponse.SC_OK);
    }

    /* ================= UTILIDADES ================= */

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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");

        return userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
