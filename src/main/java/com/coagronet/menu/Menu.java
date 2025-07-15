package com.coagronet.menu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/menu")
@CrossOrigin(origins = "*")
public class Menu {

    @GetMapping
    public ResponseEntity<String> getJson(Authentication authentication) throws IOException {
        // Obtener el rol del usuario autenticado
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // Determinar el archivo JSON según el rol
        String filename = switch (role) {
            case "ROLE_ADMINISTRADOR_SISTEMA" -> "menu_administrador_sistema.json";
            case "ROLE_ADMINISTRADOR_EMPRESA" -> "menu_administrador_empresa.json";
            case "ROLE_GERENTE" -> "menu_gerente.json";
            case "ROLE_VENDEDOR" -> "menu_vendedor.json";
            case "ROLE_COMPRADOR" -> "menu_comprador.json";
            case "ROLE_ALMACENISTA" -> "menu_almacenista.json";
            default -> "menu_administrador_sistema.json"; // O manejar error
        };

        // Leer el archivo JSON correspondiente
        ClassPathResource resource = new ClassPathResource("menus/" + filename);
        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return ResponseEntity.ok(content);
    }

}
