/*=============================================================================
 Nombre del archivo : ReporteKardexPreloadServiceTest.java
 Descripcion        : Pruebas unitarias de la precarga de filtros del reporte Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-06-19 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import com.coagronet.reports.dtos.ReporteKardexFiltroOpcionDTO;
import com.coagronet.reports.dtos.ReporteKardexPreloadDTO;
import com.coagronet.reports.exceptions.ReporteKardexPreloadException;
import com.coagronet.reports.repositories.ReporteKardexPreloadRepository;
import com.coagronet.reports.repositories.ReporteKardexPreloadRepository.UbicacionReporteRow;
import com.coagronet.utils.UserEmpresaService;

@ExtendWith(MockitoExtension.class)
class ReporteKardexPreloadServiceTest {

	private static final Long EMPRESA_ID = 452L;

	@Mock
	private ReporteKardexPreloadRepository repository;

	@Mock
	private UserEmpresaService userEmpresaService;

	@InjectMocks
	private ReporteKardexPreloadService service;

	@Test
	void shouldPreselectCompleteLocationAndLeaveDependentProductFiltersEmpty() {
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findUbicacionesActivas(EMPRESA_ID)).thenReturn(List.of(locationRow()));
		when(repository.findCategoriasActivas(EMPRESA_ID))
			.thenReturn(List.of(new ReporteKardexFiltroOpcionDTO(5L, "Insumos", null)));
		when(repository.findProduccionesActivas(EMPRESA_ID))
			.thenReturn(List.of(new ReporteKardexFiltroOpcionDTO(11L, "Produccion cafe", 10L)));

		ReporteKardexPreloadDTO response = service.preload();

		assertThat(response.ubicacionDisponible()).isTrue();
		assertThat(response.seleccionInicial().paisId()).isEqualTo(1L);
		assertThat(response.seleccionInicial().departamentoId()).isEqualTo(16L);
		assertThat(response.seleccionInicial().municipioId()).isEqualTo(36L);
		assertThat(response.seleccionInicial().sedeId()).isEqualTo(8L);
		assertThat(response.seleccionInicial().bloqueId()).isEqualTo(9L);
		assertThat(response.seleccionInicial().espacioId()).isEqualTo(10L);
		assertThat(response.seleccionInicial().almacenId()).isEqualTo(11L);
		assertThat(response.bloques()).containsExactly(new ReporteKardexFiltroOpcionDTO(9L, "Bloque A", 8L));
		assertThat(response.espacios()).containsExactly(new ReporteKardexFiltroOpcionDTO(10L, "Espacio A", 9L));
		assertThat(response.almacenes()).containsExactly(new ReporteKardexFiltroOpcionDTO(11L, "Almacen A", 10L));
		assertThat(response.productos()).isEmpty();
		assertThat(response.presentaciones()).isEmpty();
		assertThat(response.producciones()).containsExactly(new ReporteKardexFiltroOpcionDTO(11L, "Produccion cafe", 10L));
		assertDefaultDateRange(response);
	}

	@Test
	void shouldLoadProductsBySelectedCategory() {
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findProductosActivos(EMPRESA_ID, 5L))
			.thenReturn(List.of(new ReporteKardexFiltroOpcionDTO(6L, "Fertilizante", 5L)));

		List<ReporteKardexFiltroOpcionDTO> response = service.productosPorCategoria(5L);

		assertThat(response).containsExactly(new ReporteKardexFiltroOpcionDTO(6L, "Fertilizante", 5L));
	}

	@Test
	void shouldLoadPresentationsBySelectedProduct() {
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findPresentacionesActivas(EMPRESA_ID, 6L))
			.thenReturn(List.of(new ReporteKardexFiltroOpcionDTO(7L, "Bolsa 50 kg", 6L)));

		List<ReporteKardexFiltroOpcionDTO> response = service.presentacionesPorProducto(6L);

		assertThat(response).containsExactly(new ReporteKardexFiltroOpcionDTO(7L, "Bolsa 50 kg", 6L));
	}

	@Test
	void shouldStopAutomaticSelectionWhenAParentHasMultipleChildren() {
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findUbicacionesActivas(EMPRESA_ID)).thenReturn(List.of(
				locationRow(1L, "Colombia", 16L, "Huila", 36L, "Neiva", 8L, "Sede Neiva"),
				locationRow(1L, "Colombia", 18L, "Cundinamarca", 38L, "Bogota", 9L, "Sede Bogota")));
		stubEmptyProductFilters();

		ReporteKardexPreloadDTO response = service.preload();

		assertThat(response.seleccionInicial().paisId()).isEqualTo(1L);
		assertThat(response.seleccionInicial().departamentoId()).isNull();
		assertThat(response.seleccionInicial().municipioId()).isNull();
		assertThat(response.seleccionInicial().sedeId()).isNull();
		assertThat(response.seleccionInicial().bloqueId()).isNull();
		assertThat(response.seleccionInicial().espacioId()).isNull();
		assertThat(response.seleccionInicial().almacenId()).isNull();
	}

	@Test
	void shouldReturnLocationUnavailableWhenCompanyHasNoActiveSites() {
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findUbicacionesActivas(EMPRESA_ID)).thenReturn(List.of());
		stubEmptyProductFilters();

		ReporteKardexPreloadDTO response = service.preload();

		assertThat(response.ubicacionDisponible()).isFalse();
		assertThat(response.paises()).isEmpty();
		assertThat(response.departamentos()).isEmpty();
		assertThat(response.municipios()).isEmpty();
		assertThat(response.sedes()).isEmpty();
		assertThat(response.bloques()).isEmpty();
		assertThat(response.espacios()).isEmpty();
		assertThat(response.almacenes()).isEmpty();
		assertThat(response.seleccionInicial().paisId()).isNull();
		assertThat(response.seleccionInicial().departamentoId()).isNull();
		assertThat(response.seleccionInicial().municipioId()).isNull();
		assertThat(response.seleccionInicial().sedeId()).isNull();
		assertThat(response.seleccionInicial().bloqueId()).isNull();
		assertThat(response.seleccionInicial().espacioId()).isNull();
		assertThat(response.seleccionInicial().almacenId()).isNull();
	}

	@Test
	void shouldHideDatabaseDetailsWhenPreloadFails() {
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findUbicacionesActivas(EMPRESA_ID))
			.thenThrow(new DataAccessResourceFailureException("database connection details"));

		assertThatThrownBy(service::preload)
			.isInstanceOf(ReporteKardexPreloadException.class)
			.hasMessage("report.kardex.preload.error");
	}

	private void assertDefaultDateRange(ReporteKardexPreloadDTO response) {
		LocalDate today = LocalDate.now();
		assertThat(response.fechaFin()).isEqualTo(today);
		assertThat(response.fechaInicio()).isEqualTo(today.minusMonths(1).withDayOfMonth(1));
	}

	private void stubEmptyProductFilters() {
		when(repository.findCategoriasActivas(EMPRESA_ID)).thenReturn(List.of());
		when(repository.findProduccionesActivas(EMPRESA_ID)).thenReturn(List.of());
	}

	private UbicacionReporteRow locationRow() {
		return locationRow(1L, "Colombia", 16L, "Huila", 36L, "Neiva", 8L, "Sede principal");
	}

	private UbicacionReporteRow locationRow(Long paisId, String pais, Long departamentoId, String departamento,
			Long municipioId, String municipio, Long sedeId, String sede) {
		return new UbicacionReporteRow(paisId, pais, departamentoId, departamento, municipioId, municipio, sedeId, sede,
				9L, "Bloque A", 10L, "Espacio A", 11L, "Almacen A");
	}
}
