package com.coagronet.reports.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.reports.dtos.ReporteKardexFiltroOpcionDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoConsultaResponseDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoEstado;
import com.coagronet.reports.dtos.ReporteVencimientoProductoEstadoOpcionDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoFiltroDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoPreloadDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoRangoRapidoDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoResultadoDTO;
import com.coagronet.reports.dtos.ReporteVencimientoProductoSeleccionInicialDTO;
import com.coagronet.reports.exceptions.ReporteVencimientoProductoException;
import com.coagronet.reports.repositories.ReporteVencimientoProductoRepository;
import com.coagronet.reports.repositories.ReporteVencimientoProductoRepository.ResultadoVencimientoRow;
import com.coagronet.reports.repositories.ReporteVencimientoProductoRepository.UbicacionVencimientoRow;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReporteVencimientoProductoService {

	private final ReporteVencimientoProductoRepository repository;

	private final UserEmpresaService userEmpresaService;

	private final MessageSource messageSource;

	@Transactional(readOnly = true)
	public ReporteVencimientoProductoPreloadDTO preload(Locale locale) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		try {
			List<UbicacionVencimientoRow> ubicaciones = repository.findUbicacionesActivas(empresaId);
			LocalDate today = LocalDate.now();
			LocalDate endDate = today.plusMonths(1);

			List<ReporteKardexFiltroOpcionDTO> paises = distinctOptions(
					ubicaciones, UbicacionVencimientoRow::paisId, UbicacionVencimientoRow::pais, row -> null);
			List<ReporteKardexFiltroOpcionDTO> departamentos = distinctOptions(
					ubicaciones, UbicacionVencimientoRow::departamentoId, UbicacionVencimientoRow::departamento,
					UbicacionVencimientoRow::paisId);
			List<ReporteKardexFiltroOpcionDTO> municipios = distinctOptions(
					ubicaciones, UbicacionVencimientoRow::municipioId, UbicacionVencimientoRow::municipio,
					UbicacionVencimientoRow::departamentoId);
			List<ReporteKardexFiltroOpcionDTO> sedes = distinctOptions(
					ubicaciones, UbicacionVencimientoRow::sedeId, UbicacionVencimientoRow::sede,
					UbicacionVencimientoRow::municipioId);
			List<ReporteKardexFiltroOpcionDTO> bloques = distinctOptions(
					ubicaciones, UbicacionVencimientoRow::bloqueId, UbicacionVencimientoRow::bloque,
					UbicacionVencimientoRow::sedeId);
			List<ReporteKardexFiltroOpcionDTO> espacios = distinctOptions(
					ubicaciones, UbicacionVencimientoRow::espacioId, UbicacionVencimientoRow::espacio,
					UbicacionVencimientoRow::bloqueId);
			List<ReporteKardexFiltroOpcionDTO> almacenes = distinctOptions(
					ubicaciones, UbicacionVencimientoRow::almacenId, UbicacionVencimientoRow::almacen,
					UbicacionVencimientoRow::espacioId);

			return new ReporteVencimientoProductoPreloadDTO(
					today,
					endDate,
					!almacenes.isEmpty(),
					paises,
					departamentos,
					municipios,
					sedes,
					bloques,
					espacios,
					almacenes,
					repository.findCategoriasActivas(empresaId),
					List.of(),
					List.of(),
					buildRangosRapidos(today, locale),
					buildEstados(locale),
					createInitialSelection(paises, departamentos, municipios, sedes, bloques, espacios, almacenes));
		}
		catch (DataAccessException exception) {
			log.error("No fue posible precargar filtros de vencimiento para la empresa {}", empresaId, exception);
			throw new ReporteVencimientoProductoException(
					"report.vencimiento.preload.error",
					HttpStatus.INTERNAL_SERVER_ERROR,
					exception);
		}
	}

	@Transactional(readOnly = true)
	public List<ReporteKardexFiltroOpcionDTO> productosPorCategoria(Long categoriaId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return repository.findProductosActivos(empresaId, positiveOrNull(categoriaId));
	}

	@Transactional(readOnly = true)
	public List<ReporteKardexFiltroOpcionDTO> presentacionesPorProducto(Long productoId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return repository.findPresentacionesActivas(empresaId, positiveOrNull(productoId));
	}

	@Transactional(readOnly = true)
	public ReporteVencimientoProductoConsultaResponseDTO buscar(
			ReporteVencimientoProductoFiltroDTO filtro,
			Locale locale) {
		ReporteVencimientoProductoFiltroDTO normalized = normalize(filtro);
		validate(normalized);

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		LocalDate fechaGeneracion = LocalDate.now();
		try {
			List<ResultadoVencimientoRow> rows = repository.findResultados(empresaId, normalized, fechaGeneracion);
			List<ReporteVencimientoProductoResultadoDTO> resultados = rows.stream()
				.map(row -> toResultadoDTO(row, fechaGeneracion, locale))
				.toList();
			String messageKey = resultados.isEmpty()
					? "report.vencimiento.no-results"
					: "report.vencimiento.results";
			String mensaje = message(messageKey, new Object[] { resultados.size() }, locale);
			return new ReporteVencimientoProductoConsultaResponseDTO(
					fechaGeneracion,
					resultados.size(),
					mensaje,
					resultados);
		}
		catch (DataAccessException exception) {
			log.error("No fue posible consultar productos por vencer para la empresa {}", empresaId, exception);
			throw new ReporteVencimientoProductoException(
					"report.vencimiento.search.error",
					HttpStatus.INTERNAL_SERVER_ERROR,
					exception);
		}
	}

	ReporteVencimientoProductoFiltroDTO normalize(ReporteVencimientoProductoFiltroDTO filtro) {
		if (filtro == null) {
			throw new ReporteVencimientoProductoException(
					"report.vencimiento.request.required",
					HttpStatus.BAD_REQUEST);
		}
		return new ReporteVencimientoProductoFiltroDTO(
				positiveOrNull(filtro.paisId()),
				positiveOrNull(filtro.departamentoId()),
				positiveOrNull(filtro.municipioId()),
				positiveOrNull(filtro.sedeId()),
				positiveOrNull(filtro.bloqueId()),
				positiveOrNull(filtro.espacioId()),
				positiveOrNull(filtro.almacenId()),
				positiveOrNull(filtro.categoriaId()),
				positiveOrNull(filtro.productoId()),
				positiveOrNull(filtro.presentacionId()),
				filtro.fechaInicio(),
				filtro.fechaFin(),
				ReporteVencimientoProductoEstado.normalize(filtro.estado()));
	}

	private void validate(ReporteVencimientoProductoFiltroDTO filtro) {
		if (filtro.fechaInicio() == null || filtro.fechaFin() == null) {
			throw new ReporteVencimientoProductoException(
					"report.vencimiento.date-range.required",
					HttpStatus.BAD_REQUEST);
		}
		if (filtro.fechaInicio().isAfter(filtro.fechaFin())) {
			throw new ReporteVencimientoProductoException(
					"report.vencimiento.date-range.invalid",
					HttpStatus.BAD_REQUEST);
		}
		if (!filtro.tieneUbicacionMinima()) {
			throw new ReporteVencimientoProductoException(
					"report.vencimiento.location.required",
					HttpStatus.BAD_REQUEST);
		}
	}

	private ReporteVencimientoProductoResultadoDTO toResultadoDTO(
			ResultadoVencimientoRow row,
			LocalDate fechaGeneracion,
			Locale locale) {
		ReporteVencimientoProductoEstado estado = row.fechaVencimiento().isAfter(fechaGeneracion)
				? ReporteVencimientoProductoEstado.PROXIMO_A_VENCER
				: ReporteVencimientoProductoEstado.VENCIDO;
		return new ReporteVencimientoProductoResultadoDTO(
				row.kardexItemId(),
				row.producto(),
				estado.name(),
				message(estadoMessageKey(estado), null, locale),
				row.fechaVencimiento(),
				row.cantidad(),
				row.paisId(),
				row.pais(),
				row.departamentoId(),
				row.departamento(),
				row.municipioId(),
				row.municipio(),
				row.sedeId(),
				row.sede(),
				row.bloqueId(),
				row.bloque(),
				row.espacioId(),
				row.espacio(),
				row.almacenId(),
				row.almacen(),
				String.join(" / ", row.pais(), row.departamento(), row.municipio(), row.sede(), row.bloque(), row.espacio(), row.almacen()));
	}

	private List<ReporteVencimientoProductoRangoRapidoDTO> buildRangosRapidos(LocalDate today, Locale locale) {
		return List.of(
				new ReporteVencimientoProductoRangoRapidoDTO(
						"HOY",
						message("report.vencimiento.range.today", null, locale),
						today,
						today),
				new ReporteVencimientoProductoRangoRapidoDTO(
						"PROXIMOS_7_DIAS",
						message("report.vencimiento.range.next-7-days", null, locale),
						today,
						today.plusDays(7)),
				new ReporteVencimientoProductoRangoRapidoDTO(
						"PROXIMOS_15_DIAS",
						message("report.vencimiento.range.next-15-days", null, locale),
						today,
						today.plusDays(15)),
				new ReporteVencimientoProductoRangoRapidoDTO(
						"PROXIMO_MES",
						message("report.vencimiento.range.next-month", null, locale),
						today,
						today.plusMonths(1)),
				new ReporteVencimientoProductoRangoRapidoDTO(
						"PERSONALIZADO",
						message("report.vencimiento.range.custom", null, locale),
						today,
						today));
	}

	private List<ReporteVencimientoProductoEstadoOpcionDTO> buildEstados(Locale locale) {
		return List.of(
				new ReporteVencimientoProductoEstadoOpcionDTO(
						ReporteVencimientoProductoEstado.TODOS.name(),
						message("report.vencimiento.estado.todos", null, locale)),
				new ReporteVencimientoProductoEstadoOpcionDTO(
						ReporteVencimientoProductoEstado.VENCIDO.name(),
						message("report.vencimiento.estado.vencido", null, locale)),
				new ReporteVencimientoProductoEstadoOpcionDTO(
						ReporteVencimientoProductoEstado.PROXIMO_A_VENCER.name(),
						message("report.vencimiento.estado.proximo", null, locale)));
	}

	private ReporteVencimientoProductoSeleccionInicialDTO createInitialSelection(
			List<ReporteKardexFiltroOpcionDTO> paises,
			List<ReporteKardexFiltroOpcionDTO> departamentos,
			List<ReporteKardexFiltroOpcionDTO> municipios,
			List<ReporteKardexFiltroOpcionDTO> sedes,
			List<ReporteKardexFiltroOpcionDTO> bloques,
			List<ReporteKardexFiltroOpcionDTO> espacios,
			List<ReporteKardexFiltroOpcionDTO> almacenes) {
		Long paisId = onlyOptionId(paises);
		Long departamentoId = onlyChildOptionId(departamentos, paisId);
		Long municipioId = onlyChildOptionId(municipios, departamentoId);
		Long sedeId = onlyChildOptionId(sedes, municipioId);
		Long bloqueId = onlyChildOptionId(bloques, sedeId);
		Long espacioId = onlyChildOptionId(espacios, bloqueId);
		Long almacenId = onlyChildOptionId(almacenes, espacioId);
		return new ReporteVencimientoProductoSeleccionInicialDTO(
				paisId,
				departamentoId,
				municipioId,
				sedeId,
				bloqueId,
				espacioId,
				almacenId);
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
			List<UbicacionVencimientoRow> rows,
			Function<UbicacionVencimientoRow, Long> idExtractor,
			Function<UbicacionVencimientoRow, String> nameExtractor,
			Function<UbicacionVencimientoRow, Long> parentExtractor) {
		return rows.stream()
			.map(row -> new ReporteKardexFiltroOpcionDTO(
					idExtractor.apply(row),
					nameExtractor.apply(row),
					parentExtractor.apply(row)))
			.distinct()
			.sorted(Comparator.comparing(ReporteKardexFiltroOpcionDTO::nombre))
			.toList();
	}

	private Long positiveOrNull(Long value) {
		return value != null && value > 0 ? value : null;
	}

	private String estadoMessageKey(ReporteVencimientoProductoEstado estado) {
		return estado == ReporteVencimientoProductoEstado.VENCIDO
				? "report.vencimiento.estado.vencido"
				: "report.vencimiento.estado.proximo";
	}

	private String message(String key, Object[] args, Locale locale) {
		return messageSource.getMessage(key, args, key, locale == null ? Locale.getDefault() : locale);
	}

}
