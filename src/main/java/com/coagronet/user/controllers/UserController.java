package com.coagronet.user.controllers;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
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
import com.coagronet.user.dtos.UsuarioDetalleResponse;
import com.coagronet.user.dtos.UsuarioFiltroRequest;
import com.coagronet.user.dtos.UsuarioListResponse;
import com.coagronet.user.dtos.UsuarioUpdateRequest;
import com.coagronet.user.mappers.UserMapper;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.user.services.UserRegistrationService;
import com.coagronet.user.services.UserUpdateService;
import com.coagronet.user.services.UsuarioListadoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

	private final UsuarioListadoService usuarioListadoService;

	private final UserUpdateService userUpdateService;

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
					userDTOUpdate.getPersonaId(), userDTOUpdate.getUsuarioEstadoId(),
					userDTOUpdate.getPreferredLanguage() != null
							? userDTOUpdate.getPreferredLanguage()
							: user.getPreferredLanguage());

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
	@PreAuthorize("hasAuthority('USUARIO_ROL_CREATE') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<Long> registrarUsuario(
			@Valid @RequestBody UserRegistrationRequest request) {

		Long userId = userRegistrationService.registerOrUpdateUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(userId);
	}

	@Operation(summary = "Listar usuarios paginados", description = """
			Obtiene un listado paginado de usuarios aplicando filtros opcionales y reglas de visibilidad asociadas al rol del usuario autenticado.

			**Parámetros de paginación**
			Se aceptan los parámetros estándar de Spring:
			- `page` (número de página, base 0)
			- `size` (cantidad de elementos por página, valor por defecto 20)
			- `sort` (campo y dirección, p.ej. `nombre,asc`)

			**Lógica de visibilidad (Multi-Tenant)**
			- `ADMINISTRADOR_SISTEMA`: visualiza todos los usuarios de la plataforma, incluyendo todas sus asignaciones.
			- `ADMINISTRADOR_EMPRESA` o usuarios con el permiso `USUARIO_ROL_READ`: ven únicamente los usuarios que pertenecen a su empresa/tenant. En la respuesta solo se incluyen las asignaciones vinculadas a dicha empresa.
			""", tags = {
			"Usuarios" }, security = { @SecurityRequirement(name = "bearerAuth") })
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listado de usuarios retornado exitosamente. El cuerpo de la respuesta sigue la estructura de Page<UsuarioListResponse>."),
			@ApiResponse(responseCode = "400", description = "Parámetros de filtrado o paginación inválidos (ej. valor de filtro incorrecto, índice de página negativo).", content = @Content),
			@ApiResponse(responseCode = "401", description = "No autenticado. El token de acceso es faltante, expirado o inválido.", content = @Content),
			@ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no posee los roles necesarios para acceder a este recurso.", content = @Content)
	})
	@GetMapping("/api/v1/usuarios")
	@PreAuthorize("hasAuthority('USUARIO_ROL_READ') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<Page<UsuarioListResponse>> listarUsuarios(
			@ParameterObject UsuarioFiltroRequest filtro,
			@ParameterObject Pageable pageable) {

		Page<UsuarioListResponse> response = usuarioListadoService.listarUsuarios(filtro, pageable);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Listar usuarios mínimos", description = "Obtiene un listado de usuarios con campos id y nombre, filtrados por la empresa del usuario actual sin paginación.", tags = {
			"Usuarios" }, security = { @SecurityRequirement(name = "bearerAuth") })
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Listado de usuarios retornado exitosamente."),
			@ApiResponse(responseCode = "401", description = "No autenticado.", content = @Content),
			@ApiResponse(responseCode = "403", description = "Acceso denegado.", content = @Content)
	})
	@GetMapping(value = "/api/v1/usuarios", params = "fields=id,nombre")
	@PreAuthorize("hasAuthority('USUARIO_ROL_READ') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<List<UserMinimalDTO>> listarUsuariosMinimal(
			@ParameterObject UsuarioFiltroRequest filtro) {

		List<UserMinimalDTO> response = usuarioListadoService.listarUsuariosMinimal(filtro);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Obtener detalle de un usuario", description = "Retorna el detalle completo de un usuario por su ID, aplicando las reglas de visibilidad del tenant actual.", tags = {
			"Usuarios" }, security = { @SecurityRequirement(name = "bearerAuth") })
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Detalle del usuario retornado exitosamente."),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado o no tiene permisos para verlo.", content = @Content),
			@ApiResponse(responseCode = "401", description = "No autenticado.", content = @Content),
			@ApiResponse(responseCode = "403", description = "Acceso denegado.", content = @Content)
	})
	@GetMapping("/api/v1/usuarios/{requestedId}")
	@PreAuthorize("hasAuthority('USUARIO_ROL_READ') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<UsuarioDetalleResponse> obtenerUsuarioDetalle(@PathVariable Long requestedId) {
		UsuarioDetalleResponse response = usuarioListadoService.obtenerUsuarioDetalle(requestedId);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Actualizar un usuario", description = "Actualiza los detalles, información personal (Persona) y asignaciones de roles de un usuario.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Usuario actualizado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Error de validación o unicidad"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	})
	@PutMapping("/api/v1/usuarios/{requestedId}")
	@PreAuthorize("hasAuthority('USUARIO_ROL_UPDATE') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<Void> actualizarUsuario(
			@PathVariable Long requestedId,
			@Valid @RequestBody UsuarioUpdateRequest request) {
		userUpdateService.updateUserDetails(requestedId, request);
		return ResponseEntity.noContent().build();
	}
}
