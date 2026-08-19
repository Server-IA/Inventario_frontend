package com.inventario.persona.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.infrastructure.security.JwtUtil;
import com.inventario.persona.Persona;
import com.inventario.persona.dtos.PersonaDTO;
import com.inventario.persona.mappers.PersonaMapper;
import com.inventario.persona.repositories.PersonaRepository;
import com.inventario.user.User;
import com.inventario.user.repositories.UserRepository;
import com.inventario.usuarioEstado.UsuarioEstado;
import com.inventario.usuarioEstado.repositories.UsuarioEstadoRepository;

@RestController
@RequestMapping("/api/v1/personas")
public class PersonaUsuarioController {

	@Autowired
	private PersonaRepository personaRepository;

	@Autowired
	private UserRepository userRepository;

	// Suponiendo que tienes una instancia de JwtService
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UsuarioEstadoRepository usuarioEstadoRepository;

	@PostMapping("/persona-usuario")
	public ResponseEntity<Map<String, Integer>> createPersona(@RequestBody PersonaDTO newPersonaRequest,
			@RequestHeader("Authorization") String authorizationHeader) {

		// Extraer el token de la cabecera Authorization
		String token = authorizationHeader.replace("Bearer ", "").trim();

		// Extraer el username desde el JWT usando la instancia de JwtService
		String username = jwtUtil.extractUsername(token);

		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		Persona persona = PersonaMapper.INSTANCE.toEntity(newPersonaRequest);
		persona = personaRepository.save(persona);

		user.setPersona(persona);
		user.setUsuarioEstado(usuarioEstadoRepository.getReferenceById(UsuarioEstado.ID_ACTIVADO_SIN_EMPRESA));

		userRepository.save(user);

		// Devolver solo el estado del usuario en la respuesta
		Map<String, Integer> response = new HashMap<>();
		response.put("usuarioEstado", user.getUsuarioEstado().getId().intValue());
		return ResponseEntity.ok(response);
	}

}
