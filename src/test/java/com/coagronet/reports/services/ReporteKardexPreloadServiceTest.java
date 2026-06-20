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
	void shouldPreselectCompleteLocationWhenEveryLevelHasOneOption() {
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findUbicacionesActivas(EMPRESA_ID)).thenReturn(List.of(
				new UbicacionReporteRow(1L, "Colombia", 16L, "Huila", 36L, "Neiva", 8L, "Sede principal")));
		when(repository.findCategoriasActivas(EMPRESA_ID))
			.thenReturn(List.of(new ReporteKardexFiltroOpcionDTO(5L, "Insumos", null)));

		ReporteKardexPreloadDTO response = service.preload();

		assertThat(response.ubicacionDisponible()).isTrue();
		assertThat(response.seleccionInicial().paisId()).isEqualTo(1L);
		assertThat(response.seleccionInicial().departamentoId()).isEqualTo(16L);
		assertThat(response.seleccionInicial().municipioId()).isEqualTo(36L);
		assertThat(response.seleccionInicial().sedeId()).isEqualTo(8L);
		assertThat(response.productos()).isEmpty();
		assertThat(response.presentaciones()).isEmpty();
		assertDefaultDateRange(response);
	}

	@Test
	void shouldStopAutomaticSelectionWhenAParentHasMultipleChildren() {
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findUbicacionesActivas(EMPRESA_ID)).thenReturn(List.of(
				new UbicacionReporteRow(1L, "Colombia", 16L, "Huila", 36L, "Neiva", 8L, "Sede Neiva"),
				new UbicacionReporteRow(1L, "Colombia", 18L, "Cundinamarca", 38L, "Bogota", 9L, "Sede Bogota")));
		when(repository.findCategoriasActivas(EMPRESA_ID)).thenReturn(List.of());

		ReporteKardexPreloadDTO response = service.preload();

		assertThat(response.seleccionInicial().paisId()).isEqualTo(1L);
		assertThat(response.seleccionInicial().departamentoId()).isNull();
		assertThat(response.seleccionInicial().municipioId()).isNull();
		assertThat(response.seleccionInicial().sedeId()).isNull();
	}

	@Test
	void shouldReturnLocationUnavailableWhenCompanyHasNoActiveSites() {
		when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(EMPRESA_ID);
		when(repository.findUbicacionesActivas(EMPRESA_ID)).thenReturn(List.of());
		when(repository.findCategoriasActivas(EMPRESA_ID)).thenReturn(List.of());

		ReporteKardexPreloadDTO response = service.preload();

		assertThat(response.ubicacionDisponible()).isFalse();
		assertThat(response.paises()).isEmpty();
		assertThat(response.departamentos()).isEmpty();
		assertThat(response.municipios()).isEmpty();
		assertThat(response.sedes()).isEmpty();
		assertThat(response.seleccionInicial().paisId()).isNull();
		assertThat(response.seleccionInicial().departamentoId()).isNull();
		assertThat(response.seleccionInicial().municipioId()).isNull();
		assertThat(response.seleccionInicial().sedeId()).isNull();
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
}
