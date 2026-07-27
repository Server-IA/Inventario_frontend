/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoServiceTest.java
 Descripcion        : Pruebas unitarias de consulta y exportacion de vencimientos de producto.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-10 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import com.coagronet.empresa.services.EmpresaService;
import com.coagronet.reports.dtos.ReporteKardexFiltroOpcionDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoConsultaResponseDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoEstado;
import com.coagronet.reports.dtos.ReporteVencimientoProductoFiltroDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoPreloadDTO;
import com.coagronet.reports.exceptions.ReporteVencimientoProductoException;
import com.coagronet.reports.repositories.ReporteVencimientoProductoRepository;
import com.coagronet.reports.repositories.ReporteVencimientoProductoRepository.ResultadoVencimientoRow;
import com.coagronet.reports.repositories.ReporteVencimientoProductoRepository.UbicacionVencimientoRow;
import com.coagronet.utils.UserEmpresaService;

@ExtendWith(MockitoExtension.class)
class ReporteVencimientoProductoServiceTest {

	private static final Long EMPRESA_ID = 452L;

	@Mock
	private ReporteVencimientoProductoRepository repository;

	@Mock
	private UserEmpresaService userEmpresaService;

	@Mock
	private EmpresaService empresaService;

	@Mock
	private MessageSource messageSource;

	@Mock
	private DataSource dataSource;

	@InjectMocks
	private ReporteVencimientoProductoService service;

	@BeforeEach
	void setUp() {
		lenient().when(messageSource.getMessage(anyString(), any(), anyString(), any(Locale.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));
	}

	@Test
	void shouldPreselectCompleteLocationWhenEveryLevelHasOneOption() {
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findUbicacionesActivas(EMPRESA_ID)).thenReturn(List.of(ubicacion()));
		when(repository.findCategoriasActivas(EMPRESA_ID))
			.thenReturn(List.of(new ReporteKardexFiltroOpcionDTO(5L, "Insumos", null)));

		ReporteVencimientoProductoPreloadDTO response = service.preload(Locale.forLanguageTag("es"));

		assertThat(response.ubicacionDisponible()).isTrue();
		assertThat(response.seleccionInicial().paisId()).isEqualTo(1L);
		assertThat(response.seleccionInicial().departamentoId()).isEqualTo(16L);
		assertThat(response.seleccionInicial().municipioId()).isEqualTo(36L);
		assertThat(response.seleccionInicial().sedeId()).isEqualTo(8L);
		assertThat(response.seleccionInicial().bloqueId()).isEqualTo(9L);
		assertThat(response.seleccionInicial().espacioId()).isEqualTo(10L);
		assertThat(response.seleccionInicial().almacenId()).isEqualTo(11L);
		assertThat(response.productos()).isEmpty();
		assertThat(response.presentaciones()).isEmpty();
		assertThat(response.rangosRapidos()).extracting("codigo")
			.containsExactly("HOY", "PROXIMOS_7_DIAS", "PROXIMOS_15_DIAS", "PROXIMO_MES", "PERSONALIZADO");
	}

	@Test
	void shouldRejectInvalidDateRange() {
		ReporteVencimientoProductoFiltroDTO filtro = filtroBase(
				LocalDate.now().plusDays(1),
				LocalDate.now(),
				ReporteVencimientoProductoEstado.TODOS);

		assertThatThrownBy(() -> service.buscar(filtro, Locale.forLanguageTag("es")))
			.isInstanceOfSatisfying(ReporteVencimientoProductoException.class, exception -> {
				assertThat(exception.getMessage()).isEqualTo("report.vencimiento.date-range.invalid");
				assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
			});
	}

	@Test
	void shouldRejectSearchWithoutMinimumLocation() {
		ReporteVencimientoProductoFiltroDTO filtro = new ReporteVencimientoProductoFiltroDTO(
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				LocalDate.now(),
				LocalDate.now().plusDays(7),
				ReporteVencimientoProductoEstado.TODOS);

		assertThatThrownBy(() -> service.buscar(filtro, Locale.forLanguageTag("es")))
			.isInstanceOfSatisfying(ReporteVencimientoProductoException.class, exception -> {
				assertThat(exception.getMessage()).isEqualTo("report.vencimiento.location.required");
				assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
			});
	}

	@Test
	void shouldAllowSearchFilteringOnlyByCountry() {
		LocalDate today = LocalDate.now();
		ReporteVencimientoProductoFiltroDTO filtro = new ReporteVencimientoProductoFiltroDTO(
				1L,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				today,
				today.plusDays(7),
				ReporteVencimientoProductoEstado.TODOS);
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findResultados(eq(EMPRESA_ID), any(ReporteVencimientoProductoFiltroDTO.class), eq(today)))
			.thenReturn(List.of(resultado(1L, "Producto filtrado por pais", today)));

		ReporteVencimientoProductoConsultaResponseDTO response = service.buscar(filtro, Locale.forLanguageTag("es"));

		assertThat(response.total()).isEqualTo(1);
	}

	@Test
	void shouldCalculateExpiredAndSoonToExpireStatesForPreview() {
		LocalDate today = LocalDate.now();
		ReporteVencimientoProductoFiltroDTO filtro = filtroBase(
				today.minusDays(5),
				today.plusDays(5),
				ReporteVencimientoProductoEstado.TODOS);
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findResultados(eq(EMPRESA_ID), any(ReporteVencimientoProductoFiltroDTO.class), eq(today)))
			.thenReturn(List.of(
					resultado(1L, "Producto vencido", today),
					resultado(2L, "Producto proximo", today.plusDays(2))));

		ReporteVencimientoProductoConsultaResponseDTO response = service.buscar(filtro, Locale.forLanguageTag("es"));

		assertThat(response.total()).isEqualTo(2);
		assertThat(response.resultados()).extracting("estadoCodigo")
			.containsExactly("VENCIDO", "PROXIMO_A_VENCER");
	}

	@Test
	void shouldBuildSafeJasperConditionFromTypedFilters() {
		LocalDate today = LocalDate.now();
		ReporteVencimientoProductoFiltroDTO filtro = filtroBase(
				today,
				today.plusDays(15),
				ReporteVencimientoProductoEstado.PROXIMO_A_VENCER);

		String condition = service.buildCondicion(EMPRESA_ID, filtro, today);

		assertThat(condition).contains("ki.kai_empresa_id = 452");
		assertThat(condition).contains("s.sed_id = 8");
		assertThat(condition).contains("a.alm_id = 11");
		assertThat(condition).contains("ki.kai_fecha_vencimiento BETWEEN DATE '" + today + "' AND DATE '" + today.plusDays(15) + "'");
		assertThat(condition).contains("ki.kai_fecha_vencimiento > DATE '" + today + "'");
		assertThat(condition).doesNotContain("$P");
	}

	@Test
	void shouldNotGenerateReportWhenThereAreNoResults() {
		LocalDate today = LocalDate.now();
		ReporteVencimientoProductoFiltroDTO filtro = filtroBase(
			today,
			today.plusDays(15),
			ReporteVencimientoProductoEstado.TODOS);
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findResultados(eq(EMPRESA_ID), any(ReporteVencimientoProductoFiltroDTO.class), eq(today)))
			.thenReturn(List.of());

		assertThatThrownBy(() -> service.exportar(
				filtro,
				ReporteVencimientoProductoService.ReporteVencimientoProductoFormato.PDF,
				Locale.forLanguageTag("es")))
			.isInstanceOfSatisfying(ReporteVencimientoProductoException.class, exception -> {
				assertThat(exception.getMessage()).isEqualTo("report.vencimiento.no-results.export");
				assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
			});
	}

	private ReporteVencimientoProductoFiltroDTO filtroBase(
			LocalDate fechaInicio,
			LocalDate fechaFin,
			ReporteVencimientoProductoEstado estado) {
		return new ReporteVencimientoProductoFiltroDTO(
				1L,
				16L,
				36L,
				8L,
				9L,
				10L,
				11L,
				5L,
				6L,
				7L,
				fechaInicio,
				fechaFin,
				estado);
	}

	private UbicacionVencimientoRow ubicacion() {
		return new UbicacionVencimientoRow(
				1L,
				"Colombia",
				16L,
				"Huila",
				36L,
				"Neiva",
				8L,
				"Sede principal",
				9L,
				"Bloque A",
				10L,
				"Bodega central",
				11L,
				"Almacen principal");
	}

	private ResultadoVencimientoRow resultado(Long id, String producto, LocalDate fechaVencimiento) {
		return new ResultadoVencimientoRow(
				id,
				producto,
				fechaVencimiento,
				BigDecimal.TEN,
				1L,
				"Colombia",
				16L,
				"Huila",
				36L,
				"Neiva",
				8L,
				"Sede principal",
				9L,
				"Bloque A",
				10L,
				"Bodega central",
				11L,
				"Almacen principal");
	}

}
