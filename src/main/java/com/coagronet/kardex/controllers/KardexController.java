package com.coagronet.kardex.controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

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
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.articuloKardex.dtos.KardexItemResponseDto;
import com.coagronet.articuloKardex.services.ArticuloKardexService;
import com.coagronet.auditoria.RequestUtils;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.kardex.dtos.KardexListDto;
import com.coagronet.kardex.dtos.KardexRequestDTO;
import com.coagronet.kardex.dtos.MetadatosSeguridad;
import com.coagronet.kardex.services.KardexService;
import com.coagronet.utils.UriBuilderUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/kardex")
@RequiredArgsConstructor
public class KardexController {

	private final KardexService kardexService;

	private final ArticuloKardexService articuloKardexService;

	private final UriBuilderUtil uriBuilderUtil;

	private final RequestUtils requestUtils;

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> actualizarKardex(@PathVariable Long requestedId,
			@RequestBody @Valid KardexDTO kardexDTO, HttpServletRequest request) {

		String ip = requestUtils.getClientIp(request);
		String host = requestUtils.getClientHost(request);

		kardexService.update(requestedId, kardexDTO, ip, host);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{requestedId}")
	public ResponseEntity<Void> eliminarKardex(@PathVariable Long requestedId) {
		kardexService.inactivarMovimiento(requestedId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Registrar un movimiento de Kardex",
			description = "Registra un nuevo movimiento en el Kardex. Verifica reglas de negocio como la asignación de responsables para productos devolutivos.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Movimiento de Kardex registrado exitosamente."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado. Rol insuficiente."),
			@ApiResponse(responseCode = "422",
					description = "Error de validación (ej. falta de responsable para producto devolutivo o el JSON es inválido).",
					content = @Content(mediaType = "application/problem+json",
							schema = @Schema(implementation = ProblemDetail.class))) })
	@PostMapping("/movimientos")
	@PreAuthorize("hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<Void> registrarMovimiento(@Valid @RequestBody KardexRequestDTO request,
			HttpServletRequest httpRequest, Authentication authentication) {

		// 1. Extracción segura de roles (Evita NoSuchElementException y soporta múltiples
		// roles)
		String roles = authentication.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		MetadatosSeguridad metadata = new MetadatosSeguridad(authentication.getName(),
				roles.isEmpty() ? "SIN_ROL" : roles, // Fallback seguro
				httpRequest.getRemoteAddr(), httpRequest.getRemoteHost());

		// 2. Procesamiento (El tenant se inyecta automáticamente bajo el capó vía
		// ThreadLocal de Security)
		Kardex kardexGuardado = kardexService.procesarMovimientoKardex(request, metadata);

		// 3. Construcción dinámica del header 'Location' según el estándar REST
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(kardexGuardado.getId()) // Requiere que el método retorne la
													// entidad persistida
			.toUri();

		return ResponseEntity.created(location).build();
	}

	@GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
    public ResponseEntity<Page<KardexListDto>> listarMovimientos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaFin,
            @RequestParam(required = false) Long tipoMovimientoId,
            @RequestParam(required = false) Long estadoId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<KardexListDto> resultado = kardexService.listarMovimientos(
                fechaInicio, fechaFin, tipoMovimientoId, estadoId, pageable);
        
        return ResponseEntity.ok(resultado);
    }

	@GetMapping("/{kardexId}/items")
	@PreAuthorize("hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
    //@PreAuthorize("hasAuthority('placeholder')")
    public ResponseEntity<Page<KardexItemResponseDto>> listItems(
            @PathVariable Long kardexId,
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) Long estadoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<KardexItemResponseDto> results = articuloKardexService.getKardexItems(
                kardexId, productoId, estadoId, fechaInicio, fechaFin, pageable
        );

        return ResponseEntity.ok(results);
    }

}