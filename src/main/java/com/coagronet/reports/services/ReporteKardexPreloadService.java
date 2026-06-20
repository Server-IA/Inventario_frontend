package com.coagronet.reports.services;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.reports.dtos.ReporteKardexFiltroOpcionDTO;
import com.coagronet.reports.dtos.ReporteKardexPreloadDTO;
import com.coagronet.reports.dtos.ReporteKardexSeleccionInicialDTO;
import com.coagronet.reports.exceptions.ReporteKardexPreloadException;
import com.coagronet.reports.repositories.ReporteKardexPreloadRepository;
import com.coagronet.reports.repositories.ReporteKardexPreloadRepository.UbicacionReporteRow;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReporteKardexPreloadService {

	private final ReporteKardexPreloadRepository repository;

	private final UserEmpresaService userEmpresaService;

	@Transactional(readOnly = true)
	public ReporteKardexPreloadDTO preload() {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		try {
			return buildPreload(empresaId);
		}
		catch (DataAccessException exception) {
			log.error("No fue posible precargar los filtros Kardex para la empresa {}", empresaId, exception);
			throw new ReporteKardexPreloadException(exception);
		}
	}

	private ReporteKardexPreloadDTO buildPreload(Long empresaId) {
		List<UbicacionReporteRow> ubicaciones = repository.findUbicacionesActivas(empresaId);
		List<ReporteKardexFiltroOpcionDTO> paises = distinctOptions(
				ubicaciones, UbicacionReporteRow::paisId, UbicacionReporteRow::pais, row -> null);
		List<ReporteKardexFiltroOpcionDTO> departamentos = distinctOptions(
				ubicaciones, UbicacionReporteRow::departamentoId, UbicacionReporteRow::departamento,
				UbicacionReporteRow::paisId);
		List<ReporteKardexFiltroOpcionDTO> municipios = distinctOptions(
				ubicaciones, UbicacionReporteRow::municipioId, UbicacionReporteRow::municipio,
				UbicacionReporteRow::departamentoId);
		List<ReporteKardexFiltroOpcionDTO> sedes = distinctOptions(
				ubicaciones, UbicacionReporteRow::sedeId, UbicacionReporteRow::sede,
				UbicacionReporteRow::municipioId);

		ReporteKardexSeleccionInicialDTO seleccionInicial = createInitialSelection(
				paises, departamentos, municipios, sedes);

		LocalDate today = LocalDate.now(Clock.systemDefaultZone());
		LocalDate startDate = today.minusMonths(1).withDayOfMonth(1);

		return new ReporteKardexPreloadDTO(
				startDate,
				today,
				!sedes.isEmpty(),
				paises,
				departamentos,
				municipios,
				sedes,
				repository.findCategoriasActivas(empresaId),
				List.of(),
				List.of(),
				seleccionInicial);
	}

	private ReporteKardexSeleccionInicialDTO createInitialSelection(
			List<ReporteKardexFiltroOpcionDTO> paises,
			List<ReporteKardexFiltroOpcionDTO> departamentos,
			List<ReporteKardexFiltroOpcionDTO> municipios,
			List<ReporteKardexFiltroOpcionDTO> sedes) {
		Long paisId = onlyOptionId(paises);
		Long departamentoId = onlyChildOptionId(departamentos, paisId);
		Long municipioId = onlyChildOptionId(municipios, departamentoId);
		Long sedeId = onlyChildOptionId(sedes, municipioId);

		return new ReporteKardexSeleccionInicialDTO(paisId, departamentoId, municipioId, sedeId);
	}

	private Long onlyOptionId(List<ReporteKardexFiltroOpcionDTO> options) {
		return options.size() == 1 ? options.getFirst().id() : null;
	}

	private Long onlyChildOptionId(List<ReporteKardexFiltroOpcionDTO> options, Long parentId) {
		if (parentId == null) {
			return null;
		}

		List<ReporteKardexFiltroOpcionDTO> children = options.stream()
			.filter(option -> Objects.equals(option.padreId(), parentId))
			.toList();

		return onlyOptionId(children);
	}

	private List<ReporteKardexFiltroOpcionDTO> distinctOptions(
			List<UbicacionReporteRow> rows,
			Function<UbicacionReporteRow, Long> idExtractor,
			Function<UbicacionReporteRow, String> nameExtractor,
			Function<UbicacionReporteRow, Long> parentExtractor) {
		return rows.stream()
			.map(row -> new ReporteKardexFiltroOpcionDTO(
					idExtractor.apply(row),
					nameExtractor.apply(row),
					parentExtractor.apply(row)))
			.distinct()
			.toList();
	}
}
