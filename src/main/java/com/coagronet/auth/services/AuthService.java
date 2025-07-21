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
@Transactional // ensures atomicity of operations
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
    private final AuthProperties props; // e.g. defaultRole, etc.

    /* ================= REGISTRATION ================= */
    public ApiResponse register(@Valid RegisterRequestDTO dto) {

        /* 1️⃣ Does the user already exist? ---------------------------------- */
        User existing = userRepo.findByUsername(dto.getUsername()).orElse(null);

        if (existing != null) {

            /* 1a. Still pending verification → 409 Conflict + resend email */
            if (existing.getUsuarioEstado() == UsuarioEstado.PENDIENTE_VERIFICACION) {

                // resend: generate (or reuse) token and send email again
                String token = emailService.createVerificationToken(existing.getUsername());
                emailService.sendVerificationEmail(existing.getUsername(), token);

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Email already registered but not verified. Verification link has been resent.");
            }

            /* 1b. Already active/in use → 400 Bad Request */
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is already in use");
        }

        /* 2️⃣ Create a new user ---------------------------------------------- */
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setUsuarioEstado(UsuarioEstado.PENDIENTE_VERIFICACION);

        Role role = roleRepo.findByName(props.getDefaultRole())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
        user.setRoles(Set.of(role));

        /* 3️⃣ Register and send email (listener) ----------------------------- */
        registrationService.registerUser(user);

        /* 4️⃣ Success → 201 Created ------------------------------------------ */
        return new ApiResponse(
                true,
                "Verification email sent to " + user.getUsername());
    }

    /* ================= LOGIN – step 1 ================= */
    public Map<String, Object> preLogin(@Valid LoginRequestDTO dto) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        User user = (User) auth.getPrincipal();

        List<UserRole> userRoles = userRoleRepo.findByUser(user);

        List<EmpresaRolDTO> rolesByCompany = userRoles.stream()
                .map(ur -> new EmpresaRolDTO(
                        ur.getEmpresa().getId(),
                        ur.getEmpresa().getNombre(),
                        ur.getRole().getId(),
                        ur.getRole().getName()))
                .toList();

        return Map.of("rolesByCompany", rolesByCompany);
    }

    /* ================= LOGIN – step 2 ================= */
    public Map<String, Object> selectRole(@Valid SelectRoleRequestDTO dto) {

        User user = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userRoleRepo.findByUserAndEmpresaIdAndRoleId(user, dto.getEmpresaId(), dto.getRolId())
                .orElseThrow(() -> new UserRoleForbiddenException("Role/company not assigned to user"));

        String token = jwt.generateToken(user.getUsername(), dto.getEmpresaId(), dto.getRolId());

        return Map.of(
                "token", token,
                "empresaId", dto.getEmpresaId(),
                "rolId", dto.getRolId());
    }

    /* ================= CHANGE, FORGOT, AND RESET PASSWORD ================= */
    public ApiResponse changePassword(@Valid ChangePasswordRequestDTO dto) {
        User user = getCurrentUser();
        if (!encoder.matches(dto.getOldPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");

        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepo.save(user);

        return new ApiResponse(true, "Password changed successfully");
    }

    public ApiResponse forgotPassword(@Valid ForgotPasswordRequestDTO dto) {
        User user = userRepo.findByUsername(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String token = emailService.createVerificationToken(user.getUsername());
        emailService.sendResetPasswordEmail(user.getUsername(), token);

        return new ApiResponse(true, "Recovery email sent");
    }

    public ApiResponse resetPassword(@Valid ResetPasswordRequestDTO dto) {
        String username = emailService.getEmailAndInvalidateToken(dto.getToken());
        if (username == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset link");

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepo.save(user);

        return new ApiResponse(true, "Password reset successfully");
    }

    /* ================= ACCOUNT VERIFICATION ================= */
    public ApiResponse verifyUser(String token) {
        boolean ok = registrationService.activateUser(token);
        return ok
                ? new ApiResponse(true, "User activated successfully")
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
        return ud.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null ||
                auth.getPrincipal() instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");

        return userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
