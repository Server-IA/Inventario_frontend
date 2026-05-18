package com.coagronet.kardex.controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.coagronet.articuloKardex.dtos.KardexItemResponseDto;
import com.coagronet.articuloKardex.services.ArticuloKardexService;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.KardexAdminListDTO;
import com.coagronet.kardex.dtos.KardexListDTO;
import com.coagronet.kardex.dtos.KardexRequestDTO;
import com.coagronet.kardex.dtos.KardexUpdateRequestDTO;
import com.coagronet.kardex.dtos.KardexUpdateResponseDTO;
import com.coagronet.kardex.dtos.MetadatosSeguridad;
import com.coagronet.kardex.services.KardexService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/kardex")
@RequiredArgsConstructor
@Tag(name = "Kardex", description = "API para la gestión de movimientos de Kardex")
public class KardexController {

	private final KardexService kardexService;

	private final ArticuloKardexService articuloKardexService;

	@Operation(summary = "Inactivar un movimiento de Kardex", description = "Realiza una eliminación lógica (inactivación) de un movimiento de Kardex a través de su ID. "
			+
			"El sistema verifica internamente que el registro pertenezca a la empresa actual en sesión " +
			"y que se encuentre en estado activo antes de proceder.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Movimiento de Kardex inactivado exitosamente. No retorna contenido."),
			@ApiResponse(responseCode = "409", description = "Movimiento inválido. Ocurre si el Kardex no existe, ya se encuentra inactivo, "
					+
					"o no pertenece a la empresa actual (MovimientoInvalidoException)."),
			@ApiResponse(responseCode = "401", description = "No autorizado. El usuario no ha iniciado sesión o no tiene un token válido.")
	})
	@DeleteMapping("/{requestedId}")
	@PreAuthorize("hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<Void> eliminarKardex(
			@Parameter(description = "ID único del movimiento de Kardex que se desea inactivar", required = true, example = "158") @PathVariable Long requestedId) {

		kardexService.inactivarMovimiento(requestedId);

		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Registrar un movimiento de Kardex", description = "Registra un nuevo movimiento en el Kardex. Verifica reglas de negocio como la asignaci?n de responsables para productos devolutivos.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Movimiento de Kardex registrado exitosamente."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado. Rol insuficiente."),
			@ApiResponse(responseCode = "422", description = "Error de validaci?n (ej. falta de responsable para producto devolutivo o el JSON es inv?lido).", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))) })
	@PostMapping("/movimientos")
	@PreAuthorize("hasAuthority('KARDEX_CREATE') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<Void> registrarMovimiento(@Valid @RequestBody KardexRequestDTO request,
			HttpServletRequest httpRequest, Authentication authentication) {

		String roles = authentication.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		MetadatosSeguridad metadata = new MetadatosSeguridad(authentication.getName(),
				roles.isEmpty() ? "SIN_ROL" : roles, httpRequest.getRemoteAddr(), httpRequest.getRemoteHost());

		Kardex kardexGuardado = kardexService.procesarMovimientoKardex(request, metadata);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(kardexGuardado.getId())
				.toUri();

		return ResponseEntity.created(location).build();
	}

	@Operation(summary = "Listar movimientos de Kardex", description = "Obtiene una lista paginada de los movimientos de kardex. Permite filtrar por rango de fechas, tipo de movimiento y estado. Retorna la empresa asociada solo si el usuario tiene rol de ADMINISTRADOR_SISTEMA.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {
					KardexListDTO.class,
					KardexAdminListDTO.class
			}))),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado", content = @Content),
			@ApiResponse(responseCode = "403", description = "Usuario no autorizado para acceder a este recurso", content = @Content)
	})
	@GetMapping
	@PreAuthorize("hasAuthority('KARDEX_READ_ALL') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<Page<?>> listarMovimientos(
			@Parameter(description = "Fecha de inicio para el filtro (formato ISO 8601 con offset)", example = "2026-04-13T00:00:00-05:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaInicio,

			@Parameter(description = "Fecha de fin para el filtro (formato ISO 8601 con offset)", example = "2026-04-13T23:59:59-05:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaFin,

			@Parameter(description = "ID del tipo de movimiento (ej. Entrada, Salida)", example = "1") @RequestParam(required = false) Long tipoMovimientoId,

			@Parameter(description = "ID del estado del movimiento", example = "2") @RequestParam(required = false) Long estadoId,

			@ParameterObject @PageableDefault(size = 20) Pageable pageable) {

		Page<?> resultado = kardexService.listarMovimientos(fechaInicio, fechaFin, tipoMovimientoId,
				estadoId, pageable);

		return ResponseEntity.ok(resultado);
	}

	@Operation(summary = "Listar ítems de un Kardex", description = "Obtiene una lista paginada de los ítems asociados a un movimiento de Kardex específico. Permite aplicar filtros opcionales por producto, estado y rangos de fecha y hora.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista paginada de ítems recuperada exitosamente"),
			@ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o ausente", content = @Content),
			@ApiResponse(responseCode = "403", description = "Prohibido - No tiene los permisos requeridos (KARDEX_READ o roles superiores)", content = @Content)
	})
	@GetMapping("/{kardexId}/items")
	@PreAuthorize("hasAuthority('KARDEX_READ') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<Page<KardexItemResponseDto>> listItems(
			@Parameter(description = "ID del Kardex del cual se desean listar los ítems", example = "1", required = true) @PathVariable Long kardexId,

			@Parameter(description = "Filtro opcional por ID del producto", example = "15") @RequestParam(required = false) Long productoId,

			@Parameter(description = "Filtro opcional por ID del estado", example = "2") @RequestParam(required = false) Long estadoId,

			@Parameter(description = "Filtro opcional por fecha y hora de inicio (Formato ISO-8601)", example = "2026-04-01T08:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,

			@Parameter(description = "Filtro opcional por fecha y hora de fin (Formato ISO-8601)", example = "2026-04-30T18:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,

			@ParameterObject @PageableDefault(size = 20) Pageable pageable) {

		Page<KardexItemResponseDto> results = articuloKardexService.getKardexItems(kardexId, productoId, estadoId,
				fechaInicio, fechaFin, pageable);

		return ResponseEntity.ok(results);
	}

	@Operation(summary = "Obtener formulario de actualización de Kardex", description = "Recupera la información actual de un movimiento de Kardex (cabecera y artículos) para precargar los datos en un formulario de actualización.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Datos del Kardex recuperados exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KardexUpdateResponseDTO.class))),
			@ApiResponse(responseCode = "401", description = "No autenticado (Token ausente o inválido)", content = @Content),
			@ApiResponse(responseCode = "403", description = "No autorizado (El usuario no tiene el permiso KARDEX_READ o los roles necesarios)", content = @Content),
			@ApiResponse(responseCode = "404", description = "Kardex no encontrado", content = @Content)
	})
	@GetMapping("/{id}/update-form")
	@PreAuthorize("hasAuthority('KARDEX_READ') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<KardexUpdateResponseDTO> getForUpdate(
			@Parameter(description = "ID único del movimiento de Kardex a consultar", required = true, example = "1024") @PathVariable Long id) {

		KardexUpdateResponseDTO response = kardexService.getKardexForUpdate(id);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Actualizar movimiento de Kardex", description = "Actualiza la cabecera y sincroniza los artículos de un movimiento de Kardex existente. "
			+
			"Realiza inactivación lógica de los ítems removidos y maneja automáticamente la desagregación de productos devolutivos.", security = {
					@SecurityRequirement(name = "bearer-jwt") })
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Movimiento actualizado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Errores de validación en el cuerpo de la petición o lógica de negocio"),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere KARDEX_UPDATE o rol de Administrador"),
			@ApiResponse(responseCode = "404", description = "Kardex no encontrada o inactiva"),
			@ApiResponse(responseCode = "422", description = "Producto devolutivo sin responsable o relaciones invalidas") })
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('KARDEX_UPDATE') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<Void> actualizarMovimiento(@PathVariable Long id,
			@Valid @RequestBody KardexUpdateRequestDTO request, HttpServletRequest httpRequest,
			Authentication authentication) {

		String roles = authentication.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		MetadatosSeguridad metadata = new MetadatosSeguridad(authentication.getName(),
				roles.isEmpty() ? "SIN_ROL" : roles, httpRequest.getRemoteAddr(), httpRequest.getRemoteHost());

		kardexService.actualizarMovimientoKardex(id, request, metadata);

		return ResponseEntity.noContent().build();
	}

}