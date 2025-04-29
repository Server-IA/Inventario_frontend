package com.coagronet.infrastructure.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.coagronet.role.Role;
import com.coagronet.role.repositories.RoleRepository;
import com.coagronet.user.User;
import com.coagronet.user.dtos.RegisterRequestDTO;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.user.services.UserRegistrationService;
import com.coagronet.usuarioEstado.UsuarioEstado;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Value("${jwt.default_role}")
    private String default_role;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        // Validar si ya existe un usuario con ese email
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Email is already in use.");
        }

        // Crear el usuario
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUsuarioEstado(UsuarioEstado.ACTIVADO_SIN_INFO);

        // Asignar rol
        Role userRole = roleRepository.findByName(default_role)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
        user.setRoles(Set.of(userRole));

        // Guardar el usuario
        userRegistrationService.registerUser(user);

        // Crear y enviar el token de verificación
        String token = emailVerificationService.createVerificationToken(user.getUsername());
        emailVerificationService.sendVerificationEmail(user.getUsername(), token);

        return ResponseEntity.ok("Verification email sent to " + user.getUsername());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        String username = getUserName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Contraseña antigua incorrecta");
        } else {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return ResponseEntity.ok("Contraseña cambiada exitosamente");
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {
        boolean isVerified = userRegistrationService.activateUser(token);
        if (isVerified) {
            return ResponseEntity.ok("User activated successfully");
        } else {
            return ResponseEntity.status(400).body("Invalid verification link");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        String token = jwtService.createJwtToken(user.getUsername(), user.getPassword());

        // Obtener el usuario por su nombre de usuario para acceder a su estado
        User foundUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear una respuesta que incluya el token y el estado del usuario
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuarioEstado", foundUser.getUsuarioEstado().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/roles")
    public Set<String> getUserRoles() {
        System.out.println("roles()");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {

            System.out.println("aquiii");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            System.out.println(userDetails);

            return userDetails.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .collect(Collectors.toSet());
        }
        return null;
    }

    private String getUserName() {
        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            // return authentication.getName();
            currentUserName = authentication.getName();
        }
        return currentUserName;
    }

    @GetMapping("/home")
    public String home() {

        String userName = this.getUserName();
        return "Welcome to the home page! UserName: " + userName;
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = emailVerificationService.createVerificationToken(user.getUsername());
        emailVerificationService.sendResetPasswordEmail(user.getUsername(), token);

        return ResponseEntity.ok("Correo de recuperación de contraseña enviado");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        String username = emailVerificationService.getEmailAndInvalidateToken(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enlace de recuperación inválido o expirado");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña restablecida exitosamente");
    }

}
