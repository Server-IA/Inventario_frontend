/*=============================================================================
 Nombre del archivo : ReportePedidoControllerTest.java
 Descripcion        : Pruebas del contrato HTTP del controlador de reporte de pedido.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-18 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.inventario.infrastructure.configuration.CorsProperties;
import com.inventario.infrastructure.i18n.LocaleResolutionService;
import com.inventario.infrastructure.security.JwtAuthenticationFilter;
import com.inventario.infrastructure.security.MyUserDetailsService;
import com.inventario.reports.dtos.ReportePedidoFiltroDTO;
import com.inventario.reports.services.ReportePedidoService;
import com.inventario.reports.services.ReportePedidoService.ReportePedidoArchivo;
import com.inventario.reports.services.ReportePedidoService.ReportePedidoFormato;
import com.inventario.reports.services.ReportService;

@WebMvcTest(
		controllers = { ReportePedidoController.class, ReportController.class },
		excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportePedidoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ReportePedidoService service;

	@MockBean
	private ReportService legacyReportService;

	@MockBean
	private LocaleResolutionService localeResolutionService;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockBean
	private MyUserDetailsService myUserDetailsService;

	@MockBean
	private CorsProperties corsProperties;

	@BeforeEach
	void setUp() {
		when(corsProperties.getAllowedOrigins()).thenReturn(List.of());
		when(localeResolutionService.resolveForHttpRequest(any(), any()))
			.thenReturn(Locale.forLanguageTag("es"));
	}

	@Test
	void shouldDownloadPdfWithAttachmentHeaders() throws Exception {
		byte[] pdf = "%PDF-1.7".getBytes();
		when(service.exportar(
				any(ReportePedidoFiltroDTO.class),
				eq(ReportePedidoFormato.PDF),
				any(Locale.class)))
			.thenReturn(new ReportePedidoArchivo(pdf, "reporte_pedido_20260718_103000.pdf", MediaType.APPLICATION_PDF));

		mockMvc.perform(post("/api/v2/report/pedido/exportar")
				.queryParam("formato", "PDF")
				.header(HttpHeaders.ACCEPT_LANGUAGE, "es")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "pedidoIds": [10, 20],
						  "fechaInicio": "2026-07-01",
						  "fechaFin": "2026-07-31"
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_PDF))
			.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"reporte_pedido_20260718_103000.pdf\""))
			.andExpect(content().bytes(pdf));
	}

	@Test
	void shouldReturnGenericLocalizedErrorForAnInvalidFormat() throws Exception {
		mockMvc.perform(post("/api/v2/report/pedido/exportar")
				.queryParam("formato", "CSV")
				.header(HttpHeaders.ACCEPT_LANGUAGE, "es")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"pedidoIds\":[10]}"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
			.andExpect(jsonPath("$.detail")
					.value("El formato solicitado no es valido. Use PDF o EXCEL."));
	}

	@Test
	void shouldRouteTheHistoricalBaseUrlToTheTypedPedidoController() throws Exception {
		byte[] pdf = "%PDF-1.7".getBytes();
		when(service.exportar(
				any(ReportePedidoFiltroDTO.class),
				eq(ReportePedidoFormato.PDF),
				any(Locale.class)))
			.thenReturn(new ReportePedidoArchivo(pdf, "reporte_pedido.pdf", MediaType.APPLICATION_PDF));

		mockMvc.perform(post("/api/v2/report/pedido")
				.header(HttpHeaders.ACCEPT_LANGUAGE, "es")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"pedidoIds\":[10]}"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_PDF))
			.andExpect(content().bytes(pdf));
		verifyNoInteractions(legacyReportService);
	}

}
