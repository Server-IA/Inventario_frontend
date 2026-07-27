/*=============================================================================
 Nombre del archivo : ReportePedidoServiceTest.java
 Descripcion        : Pruebas unitarias de consulta, exportacion y auditoria de pedidos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-18 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.reports.dtos.ReportePedidoConsultaResponseDTO;
import com.coagronet.reports.dtos.ReportePedidoFiltroDTO;
import com.coagronet.reports.exceptions.ReportePedidoException;
import com.coagronet.reports.repositories.ReportePedidoAuditoriaRepository;
import com.coagronet.reports.repositories.ReportePedidoRepository;
import com.coagronet.reports.repositories.ReportePedidoRepository.PedidoReporteRow;
import com.coagronet.user.User;
import com.coagronet.utils.UserEmpresaService;

@ExtendWith(MockitoExtension.class)
class ReportePedidoServiceTest {

	private static final Long EMPRESA_ID = 452L;

	@Mock
	private ReportePedidoRepository repository;

	@Mock
	private ReportePedidoAuditoriaRepository auditoriaRepository;

	@Mock
	private UserEmpresaService userEmpresaService;

	@Mock
	private AuthenticationService authenticationService;

	@Mock
	private MessageSource messageSource;

	@InjectMocks
	private ReportePedidoService service;

	@BeforeEach
	void setUp() {
		lenient().when(messageSource.getMessage(anyString(), any(), anyString(), any(Locale.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));
		lenient().when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
	}

	@Test
	void shouldReturnExactIndependentTotalsForEachOrder() {
		ReportePedidoFiltroDTO filter = filter();
		when(repository.findResultados(eq(EMPRESA_ID), any(ReportePedidoFiltroDTO.class))).thenReturn(List.of(
				row(10L, 101L, new BigDecimal("3.125"), "kg"),
				row(10L, 102L, new BigDecimal("2.875"), "kg"),
				row(20L, 201L, new BigDecimal("9.50"), "l")));

		ReportePedidoConsultaResponseDTO response = service.resumen(filter, Locale.forLanguageTag("es"));

		assertThat(response.totalPedidos()).isEqualTo(2);
		assertThat(response.pedidos()).extracting("pedidoId").containsExactly(10L, 20L);
		assertThat(response.pedidos().get(0).cantidadProductos()).isEqualTo(2);
		assertThat(response.pedidos().get(0).totalCantidad()).isEqualByComparingTo("6.000");
		assertThat(response.pedidos().get(1).totalCantidad()).isEqualByComparingTo("9.50");
		verify(repository).findResultados(eq(EMPRESA_ID), any(ReportePedidoFiltroDTO.class));
	}

	@Test
	void shouldSetTotalToZeroAndWarnWhenAnItemHasNoUnit() {
		when(repository.findResultados(eq(EMPRESA_ID), any(ReportePedidoFiltroDTO.class))).thenReturn(List.of(
				row(10L, 101L, new BigDecimal("3.125"), "kg"),
				row(10L, 102L, new BigDecimal("2.875"), null)));

		ReportePedidoConsultaResponseDTO response = service.resumen(filter(), Locale.forLanguageTag("es"));

		assertThat(response.pedidos().getFirst().unidadFaltante()).isTrue();
		assertThat(response.pedidos().getFirst().totalCantidad()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(response.pedidos().getFirst().advertencia())
			.isEqualTo("report.pedido.warning.unit-missing");
	}

	@Test
	void shouldKeepAnEmptyOrderWithZeroProductsAndZeroTotal() {
		when(repository.findResultados(eq(EMPRESA_ID), any(ReportePedidoFiltroDTO.class))).thenReturn(List.of(
				row(10L, null, null, null)));

		ReportePedidoConsultaResponseDTO response = service.resumen(filter(), Locale.forLanguageTag("es"));

		assertThat(response.pedidos()).hasSize(1);
		assertThat(response.pedidos().getFirst().pedidoSinProductos()).isTrue();
		assertThat(response.pedidos().getFirst().cantidadProductos()).isZero();
		assertThat(response.pedidos().getFirst().totalCantidad()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(response.pedidos().getFirst().advertencia())
			.isEqualTo("report.pedido.warning.empty");
	}

	@Test
	void shouldNormalizeOrderIdsAndRejectAnInvalidDateRange() {
		ReportePedidoFiltroDTO normalized = service.normalizeAndValidate(new ReportePedidoFiltroDTO(
				List.of(5L, 5L, -1L, 8L), null, null, null,
				null, null, null, null, null, null, null));

		assertThat(normalized.pedidoIds()).containsExactly(5L, 8L);

		ReportePedidoFiltroDTO invalid = new ReportePedidoFiltroDTO(
				List.of(), null, LocalDate.of(2026, 7, 20), LocalDate.of(2026, 7, 19),
				null, null, null, null, null, null, null);
		assertThatThrownBy(() -> service.normalizeAndValidate(invalid))
			.isInstanceOfSatisfying(ReportePedidoException.class, exception -> {
				assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
				assertThat(exception.getMessage()).isEqualTo("report.pedido.date-range.invalid");
			});
	}

	@Test
	void shouldGeneratePdfAndAuditEveryIncludedOrderAfterSuccess() {
		when(repository.findResultados(eq(EMPRESA_ID), any(ReportePedidoFiltroDTO.class))).thenReturn(List.of(
				row(10L, 101L, new BigDecimal("3.125"), "kg"),
				row(20L, 201L, new BigDecimal("9.50"), "l")));
		when(authenticationService.getAuthenticatedUser()).thenReturn(User.builder()
			.id(77L)
			.username("auditor@coagronet.com")
			.build());

		ReportePedidoService.ReportePedidoArchivo archivo = service.exportar(
				filter(),
				ReportePedidoService.ReportePedidoFormato.PDF,
				Locale.forLanguageTag("es"));

		assertThat(archivo.mediaType().toString()).isEqualTo("application/pdf");
		assertThat(archivo.nombreArchivo()).startsWith("reporte_pedido_").endsWith(".pdf");
		assertThat(archivo.contenido()).startsWith("%PDF".getBytes());
		verify(auditoriaRepository).registrar(
				any(UUID.class),
				eq(EMPRESA_ID),
				eq(77L),
				eq("auditor@coagronet.com"),
				any(OffsetDateTime.class),
				eq(List.of(10L, 20L)),
				eq("PDF"));
	}

	@Test
	void shouldGenerateExcelWithTheExpectedFormatAndAuditValue() {
		when(repository.findResultados(eq(EMPRESA_ID), any(ReportePedidoFiltroDTO.class))).thenReturn(List.of(
				row(10L, 101L, new BigDecimal("3.125"), "kg")));
		when(authenticationService.getAuthenticatedUser()).thenReturn(User.builder()
			.id(77L)
			.username("auditor@coagronet.com")
			.build());

		ReportePedidoService.ReportePedidoArchivo archivo = service.exportar(
				filter(),
				ReportePedidoService.ReportePedidoFormato.EXCEL,
				Locale.forLanguageTag("es"));

		assertThat(archivo.mediaType().toString())
			.isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		assertThat(archivo.nombreArchivo()).endsWith(".xlsx");
		assertThat(archivo.contenido()).startsWith(new byte[] { 'P', 'K' });
		verify(auditoriaRepository).registrar(
				any(UUID.class),
				eq(EMPRESA_ID),
				eq(77L),
				eq("auditor@coagronet.com"),
				any(OffsetDateTime.class),
				eq(List.of(10L)),
				eq("EXCEL"));
	}

	private ReportePedidoFiltroDTO filter() {
		return new ReportePedidoFiltroDTO(
				List.of(10L, 20L), null, null, null,
				null, null, null, null, null, null, null);
	}

	private PedidoReporteRow row(Long pedidoId, Long itemId, BigDecimal quantity, String unit) {
		return new PedidoReporteRow(
				pedidoId,
				LocalDateTime.of(2026, 7, 18, 10, 30),
				"Aprobado",
				"Coagronet",
				"contacto@coagronet.com",
				"3000000000",
				null,
				"Persona Responsable",
				"Neiva",
				"Sede principal",
				"Bloque A",
				"Espacio A",
				"Almacen A",
				itemId,
				itemId == null ? null : itemId + 1000,
				itemId == null ? null : "Producto " + itemId,
				quantity,
				unit);
	}

}
