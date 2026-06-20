package com.coagronet.reports.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.reports.dtos.ReporteKardexPreloadDTO;
import com.coagronet.reports.services.ReporteKardexPreloadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/report/kardex")
@RequiredArgsConstructor
@Tag(name = "Reporte Kardex", description = "API para inicializar los filtros del reporte Kardex")
public class ReporteKardexPreloadController {

	private final ReporteKardexPreloadService preloadService;

	@Operation(
			summary = "Precargar filtros del reporte Kardex",
			description = "Obtiene ubicaciones activas asociadas a las sedes de la empresa, categorias activas "
					+ "y el rango de fechas predeterminado. Productos y presentaciones se retornan vacios "
					+ "hasta que el usuario seleccione una categoria.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Filtros precargados exitosamente"),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
			@ApiResponse(responseCode = "403", description = "Usuario sin permisos para consultar reportes"),
			@ApiResponse(responseCode = "500", description = "No fue posible precargar los filtros")
	})
	@GetMapping("/filtros")
	@PreAuthorize("hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA', 'GERENTE')")
	public ResponseEntity<ReporteKardexPreloadDTO> preload() {
		return ResponseEntity.ok(preloadService.preload());
	}
}
