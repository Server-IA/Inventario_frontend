package com.coagronet.empresa.controllers;

import java.util.HashMap;
import java.util.Map;

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
import com.coagronet.role.Role;
import com.coagronet.role.repositories.RoleRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.userRole.UserRole;
import com.coagronet.userRole.repositories.UserRoleRepository;
import com.coagronet.usuarioEstado.UsuarioEstado;

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
	private UserRoleRepository userRoleRepository;

	@Autowired
	private RoleRepository roleRepository;

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

		// Crear y asignar rol de administrador a la empresa creada
		Role adminRole = roleRepository.findByName("ROLE_ADMINISTRADOR_EMPRESA")
			.orElseThrow(() -> new RuntimeException("Rol de administrador no encontrado"));

		UserRole userRole = new UserRole(user, adminRole, savedEmpresa);
		userRoleRepository.save(userRole);

		// Crear el mapa para la respuesta
		Map<String, Integer> response = new HashMap<>();
		response.put("usuarioEstado", user.getUsuarioEstado().getId().intValue());

		// Retornar la respuesta con solo el estado del usuario
		return ResponseEntity.ok(response);
	}

}
