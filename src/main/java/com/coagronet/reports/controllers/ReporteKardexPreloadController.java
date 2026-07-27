/*=============================================================================
 Nombre del archivo : ReporteKardexPreloadController.java
 Descripcion        : Controlador REST para la precarga de filtros del reporte Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-06-19 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.reports.dtos.ReporteKardexFiltroOpcionDTO;
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

	private static final String REPORT_AUTH = "hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA', 'GERENTE')";

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
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<ReporteKardexPreloadDTO> preload() {
		return ResponseEntity.ok(preloadService.preload());
	}

	@Operation(
			summary = "Cargar productos por categoria",
			description = "Carga la cascada Producto a partir de Categoria de Producto.")
	@GetMapping("/productos")
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<List<ReporteKardexFiltroOpcionDTO>> productos(
			@RequestParam(required = false) Long categoriaId) {
		return ResponseEntity.ok(preloadService.productosPorCategoria(categoriaId));
	}

	@Operation(
			summary = "Cargar presentaciones por producto",
			description = "Carga la cascada Presentacion a partir de Producto.")
	@GetMapping("/presentaciones")
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<List<ReporteKardexFiltroOpcionDTO>> presentaciones(
			@RequestParam(required = false) Long productoId) {
		return ResponseEntity.ok(preloadService.presentacionesPorProducto(productoId));
	}
}
