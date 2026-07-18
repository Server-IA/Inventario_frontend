package com.coagronet.reports.controllers;

import java.util.List;
import java.util.Locale;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.reports.dtos.ReporteKardexFiltroOpcionDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoConsultaResponseDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoFiltroDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoPreloadDTO;
import com.coagronet.reports.services.ReporteVencimientoProductoService;
import com.coagronet.reports.services.ReporteVencimientoProductoService.ReporteVencimientoProductoArchivo;
import com.coagronet.reports.services.ReporteVencimientoProductoService.ReporteVencimientoProductoFormato;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/report/vencimiento-producto")
@RequiredArgsConstructor
@Tag(name = "Reporte Vencimiento Producto", description = "API para consultar y exportar productos vencidos o proximos a vencer")
public class ReporteVencimientoProductoController {

	private static final String REPORT_AUTH = "hasAuthority('KARDEX_READ') or hasAuthority('KARDEX_READ_ALL') "
			+ "or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA', 'GERENTE', 'ALMACENISTA')";

	private final ReporteVencimientoProductoService service;

	@Operation(
			summary = "Precargar filtros del reporte Vencimiento Producto",
			description = "Carga ubicaciones reales con almacenes activos, categorias de producto, rangos rapidos, estados y seleccion inicial inteligente.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Filtros precargados exitosamente"),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
			@ApiResponse(responseCode = "403", description = "Usuario sin permisos de inventario"),
			@ApiResponse(responseCode = "500", description = "No fue posible precargar los filtros")
	})
	@GetMapping("/filtros")
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<ReporteVencimientoProductoPreloadDTO> preload(Locale locale) {
		return ResponseEntity.ok(service.preload(locale));
	}

	@Operation(summary = "Cargar productos por categoria", description = "Carga la cascada Producto a partir de Categoria de Producto.")
	@GetMapping("/productos")
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<List<ReporteKardexFiltroOpcionDTO>> productos(
			@RequestParam(required = false) Long categoriaId) {
		return ResponseEntity.ok(service.productosPorCategoria(categoriaId));
	}

	@Operation(summary = "Cargar presentaciones por producto", description = "Carga la cascada Presentacion a partir de Producto.")
	@GetMapping("/presentaciones")
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<List<ReporteKardexFiltroOpcionDTO>> presentaciones(
			@RequestParam(required = false) Long productoId) {
		return ResponseEntity.ok(service.presentacionesPorProducto(productoId));
	}

	@Operation(
			summary = "Consultar productos vencidos o proximos a vencer",
			description = "Devuelve la previsualizacion con los mismos datos base que se exportan en PDF o Excel.")
	@PostMapping("/buscar")
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<ReporteVencimientoProductoConsultaResponseDTO> buscar(
			@Valid @RequestBody ReporteVencimientoProductoFiltroDTO filtro,
			Locale locale) {
		return ResponseEntity.ok(service.buscar(filtro, locale));
	}

	@Operation(
			summary = "Exportar reporte Vencimiento Producto",
			description = "Genera el reporte con JasperReports en formato PDF o Excel. El formato por defecto es PDF.")
	@PostMapping("/exportar")
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<byte[]> exportar(
			@Valid @RequestBody ReporteVencimientoProductoFiltroDTO filtro,
			@RequestParam(name = "formato", defaultValue = "PDF") String formato,
			Locale locale) {
		ReporteVencimientoProductoArchivo archivo = service.exportar(
				filtro,
				ReporteVencimientoProductoFormato.parse(formato),
				locale);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(archivo.mediaType());
		headers.setContentDisposition(ContentDisposition.attachment().filename(archivo.nombreArchivo()).build());
		return ResponseEntity.ok().headers(headers).body(archivo.contenido());
	}

}
