package com.coagronet.empresa.controllers;

import java.util.HashMap;
import java.util.Map;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.dtos.EmpresaDTO;
import com.coagronet.empresa.mappers.EmpresaMapper;
import com.coagronet.empresa.services.EmpresaService;
import com.coagronet.infrastructure.security.JwtService;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.usuarioEstado.UsuarioEstado;
import com.coagronet.usuariorol.UsuarioRol;
import com.coagronet.usuariorol.repositories.UsuarioRolRepository;

@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaUsuarioController {

	@Autowired
	private EmpresaService empresaService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UsuarioRolRepository usuarioRolRepository;

	@Transactional
	@PostMapping("/empresa-usuario")
	public ResponseEntity<Map<String, Integer>> createEmpresa(@RequestBody EmpresaDTO empresaDTO,
			@RequestHeader("Authorization") String authorizationHeader) {

		// Extraer el token de la cabecera Authorization
		String token = authorizationHeader.replace("Bearer ", "").trim();

		// Extraer el username desde el JWT usando la instancia de JwtService
		String username = jwtService.extractUsername(token);

		// Obtener el usuario asociado usando el username
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		// Convertir DTO a entidad Empresa
		Empresa empresa = EmpresaMapper.INSTANCE.toEmpresa(empresaDTO);

		// Asociar la persona al usuario
		empresa.setPersona(user.getPersona());

		// Guardar la entidad Empresa
		Empresa savedEmpresa = empresaService.save(empresa);

		// Cambiar el estado del usuario a "4"
		user.setUsuarioEstado(UsuarioEstado.ACTIVADO_CON_EMPRESA);
		userRepository.save(user);		

		UsuarioRol usuarioRol = usuarioRolRepository.findByUser(user);

		usuarioRol.setEmpresa(savedEmpresa);
		usuarioRolRepository.save(usuarioRol);

		// Crear el mapa para la respuesta
		Map<String, Integer> response = new HashMap<>();
		response.put("usuarioEstado", user.getUsuarioEstado().getId().intValue());

		// Retornar la respuesta con solo el estado del usuario
		return ResponseEntity.ok(response);
	}

}
