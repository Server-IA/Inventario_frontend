package com.coagronet.persona.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.infrastructure.security.JwtService;
import com.coagronet.persona.Persona;
import com.coagronet.persona.dtos.PersonaDTO;
import com.coagronet.persona.mappers.PersonaMapper;
import com.coagronet.persona.repositories.PersonaRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.usuarioEstado.UsuarioEstado;

@RestController
@RequestMapping("/api/v1/personas")
@CrossOrigin(origins = "*")
public class PersonaUsuarioController {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private UserRepository userRepository;

    // Suponiendo que tienes una instancia de JwtService
    @Autowired
    private JwtService jwtService;

    @PostMapping("/persona-usuario")
    public ResponseEntity<Map<String, Integer>> createPersona(
            @RequestBody PersonaDTO personaDTO,
            @RequestHeader("Authorization") String authorizationHeader) {

        // Extraer el token de la cabecera Authorization
        String token = authorizationHeader.replace("Bearer ", "").trim();

        // Extraer el username desde el JWT usando la instancia de JwtService
        String username = jwtService.extractUsername(token);

        // Lógica de tu método
        Persona persona = PersonaMapper.INSTANCE.toEntity(personaDTO);
        personaRepository.save(persona);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setUsuarioEstado(UsuarioEstado.ACTIVADO_SIN_EMPRESA);
        user.setPersona(persona);
        userRepository.save(user);

        // Devolver solo el estado del usuario en la respuesta
        Map<String, Integer> response = new HashMap<>();
        response.put("usuarioEstado", user.getUsuarioEstado().getId().intValue());

        return ResponseEntity.ok(response);
    }

}
