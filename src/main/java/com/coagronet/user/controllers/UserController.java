package com.coagronet.user.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.user.User;
import com.coagronet.user.dtos.UserDTO;
import com.coagronet.user.dtos.UserMinimalDTO;
import com.coagronet.user.dtos.UserRegistrationRequest;
import com.coagronet.user.mappers.UserMapper;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.user.services.UserRegistrationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Usuarios", description = "API para la gestión y registro de usuarios")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

	private final UserRepository userRepository;

	private final UserMapper userMapper;

	private final PasswordEncoder passwordEncoder;

	private final UserRegistrationService userRegistrationService;

	@GetMapping("/api/v1/user/{requestedId}")
	private ResponseEntity<UserDTO> findById(@PathVariable Long requestedId) {
		return userRepository.findById(requestedId)
				.map(userMapper::toDto)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/api/v1/user")
	private ResponseEntity<Page<UserDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<UserDTO> page = userRepository.findByUsuarioEstadoIdGreaterThanEqual(0, pageable).map(userMapper::toDto);
		return page.hasContent() ? ResponseEntity.ok(page) : ResponseEntity.noContent().build();
	}

	@GetMapping("/api/v1/user/minimal")
	private ResponseEntity<Page<UserMinimalDTO>> findAllMinimal(@PageableDefault Pageable pageable) {
		Page<UserMinimalDTO> page = userRepository.findByUsuarioEstadoIdGreaterThanEqual(0, pageable)
				.map(userMapper::toMinimalDTO);
		return page.hasContent() ? ResponseEntity.ok(page) : ResponseEntity.noContent().build();
	}

	@PutMapping("/api/v1/user/{requestedId}")
	private ResponseEntity<Void> putUser(@PathVariable Long requestedId, @RequestBody UserDTO userDTOUpdate) {
		User user = userRepository.findById(requestedId).orElse(null);
		if (null != user) {
			String encodedPassword = passwordEncoder.encode(userDTOUpdate.getPassword());
			UserDTO updatedUserDTO = new UserDTO(requestedId, encodedPassword, userDTOUpdate.getUsername(),
					userDTOUpdate.getPersonaId(), userDTOUpdate.getUsuarioEstadoId());
			User updatedUser = userMapper.toEntity(updatedUserDTO);
			userRepository.save(updatedUser);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/api/v1/user/{id}")
	private ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		try {
			if (userRepository.existsById(id)) {
				userRepository.deleteById(id);
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario junto con su registro de Persona si no existe. Además, asigna los roles correspondientes en las empresas especificadas. Valida que el nombre de usuario no exista y que las fechas de los contratos sean congruentes.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Usuario registrado y roles asignados exitosamente"),
			@ApiResponse(responseCode = "400", description = "Error de validación en los datos enviados o reglas de negocio (ej. username ya registrado, fechas de contrato inválidas, rol inactivo)"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado. Se requieren privilegios de administrador o permisos de creación.")
	})
	@PostMapping("/api/v1/usuarios/registro")
	@PreAuthorize("hasAuthority('USUARIO_ROL_CREATE') or hasRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<Void> registrarUsuario(
			@Valid @RequestBody UserRegistrationRequest request) {

		userRegistrationService.registerOrUpdateUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
