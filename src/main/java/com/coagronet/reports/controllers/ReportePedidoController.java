package com.coagronet.reports.controllers;

import java.time.Instant;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.infrastructure.i18n.LocaleResolutionService;
import com.coagronet.reports.dtos.ReportePedidoConsultaResponseDTO;
import com.coagronet.reports.dtos.ReportePedidoFiltroDTO;
import com.coagronet.reports.dtos.ReportePedidoPreloadDTO;
import com.coagronet.reports.exceptions.ReportePedidoException;
import com.coagronet.reports.services.ReportePedidoService;
import com.coagronet.reports.services.ReportePedidoService.ReportePedidoArchivo;
import com.coagronet.reports.services.ReportePedidoService.ReportePedidoFormato;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/report/pedido")
@RequiredArgsConstructor
@Tag(name = "Reporte Pedido", description = "API para consultar totales y exportar pedidos")
public class ReportePedidoController {

	private static final String REPORT_AUTH = "hasAnyRole('ADMINISTRADOR_SISTEMA', "
			+ "'ADMINISTRADOR_EMPRESA', 'GERENTE', 'ALMACENISTA')";

	private final ReportePedidoService service;
	private final LocaleResolutionService localeResolutionService;
	private final MessageSource messageSource;

	@Operation(summary = "Precargar filtros del reporte de pedido")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Filtros cargados"),
		@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
		@ApiResponse(responseCode = "403", description = "Usuario sin permisos"),
		@ApiResponse(responseCode = "500", description = "No fue posible cargar los filtros")
	})
	@GetMapping("/filtros")
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<ReportePedidoPreloadDTO> preload() {
		return ResponseEntity.ok(service.preload());
	}

	@Operation(
			summary = "Visualizar el resumen de totales de los pedidos",
			description = "Devuelve identificador, fecha, almacen, cantidad de productos y total exacto por pedido.")
	@PostMapping("/resumen")
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<ReportePedidoConsultaResponseDTO> resumen(
			@Valid @RequestBody ReportePedidoFiltroDTO filtro,
			@RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage,
			Authentication authentication) {
		return ResponseEntity.ok(service.resumen(
				filtro, resolveLocale(acceptLanguage, authentication)));
	}

	@Operation(
			summary = "Generar y descargar el reporte de pedido",
			description = "Exporta los pedidos en PDF o Excel y registra la auditoria de la generacion exitosa.")
	@PostMapping("/exportar")
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<byte[]> exportar(
			@Valid @RequestBody ReportePedidoFiltroDTO filtro,
			@RequestParam(name = "formato", defaultValue = "PDF") String formato,
			@RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage,
			Authentication authentication) {
		ReportePedidoArchivo archivo = service.exportar(
				filtro,
				ReportePedidoFormato.parse(formato),
				resolveLocale(acceptLanguage, authentication));
		return downloadResponse(archivo);
	}

	@Operation(
			summary = "Generar reporte de pedido en PDF",
			description = "Alias compatible de la ruta historica; utiliza el contrato tipado y seguro de RF-042.")
	@PostMapping
	@PreAuthorize(REPORT_AUTH)
	public ResponseEntity<byte[]> exportarPdf(
			@Valid @RequestBody ReportePedidoFiltroDTO filtro,
			@RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage,
			Authentication authentication) {
		ReportePedidoArchivo archivo = service.exportar(
				filtro,
				ReportePedidoFormato.PDF,
				resolveLocale(acceptLanguage, authentication));
		return downloadResponse(archivo);
	}

	private ResponseEntity<byte[]> downloadResponse(ReportePedidoArchivo archivo) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(archivo.mediaType());
		headers.setContentDisposition(ContentDisposition.attachment()
				.filename(archivo.nombreArchivo())
				.build());
		return ResponseEntity.ok().headers(headers).body(archivo.contenido());
	}

	private Locale resolveLocale(String acceptLanguage, Authentication authentication) {
		String username = authentication == null ? null : authentication.getName();
		return localeResolutionService.resolveForHttpRequest(acceptLanguage, username);
	}

	@ExceptionHandler(ReportePedidoException.class)
	public ProblemDetail handleReportePedidoException(
			ReportePedidoException exception,
			Locale locale) {
		String detail = messageSource.getMessage(
				exception.getMessage(),
				null,
				messageSource.getMessage(
						"report.pedido.error.generic",
						null,
						"No fue posible procesar el reporte.",
						locale),
				locale);
		String title = messageSource.getMessage(
				"report.pedido.error.title", null, "Error de reporte", locale);
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getStatus(), detail);
		problemDetail.setTitle(title);
		problemDetail.setProperty("timestamp", Instant.now());
		return problemDetail;
	}

}
