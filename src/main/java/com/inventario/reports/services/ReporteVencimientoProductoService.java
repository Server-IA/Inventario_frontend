/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoService.java
 Descripcion        : Servicio para consultar y exportar vencimientos de producto.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-10 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.empresa.services.EmpresaService;
import com.inventario.reports.dtos.ReporteKardexFiltroOpcionDTO;
import com.inventario.reports.dtos.ReporteVencimientoProductoConsultaResponseDTO;
import com.inventario.reports.dtos.ReporteVencimientoProductoEstado;
import com.inventario.reports.dtos.ReporteVencimientoProductoEstadoOpcionDTO;
import com.inventario.reports.dtos.ReporteVencimientoProductoFiltroDTO;
import com.inventario.reports.dtos.ReporteVencimientoProductoPreloadDTO;
import com.inventario.reports.dtos.ReporteVencimientoProductoRangoRapidoDTO;
import com.inventario.reports.dtos.ReporteVencimientoProductoResultadoDTO;
import com.inventario.reports.dtos.ReporteVencimientoProductoSeleccionInicialDTO;
import com.inventario.reports.exceptions.ReporteVencimientoProductoException;
import com.inventario.reports.repositories.ReporteVencimientoProductoRepository;
import com.inventario.reports.repositories.ReporteVencimientoProductoRepository.ResultadoVencimientoRow;
import com.inventario.reports.repositories.ReporteVencimientoProductoRepository.UbicacionVencimientoRow;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReporteVencimientoProductoService {

	private static final String REPORTE_NOMBRE = "producto_vencimiento";

	private static final String REPORTE_RUTA = "reports/" + REPORTE_NOMBRE + ".jrxml";

	private static final Long ESTADO_ACTIVO = 1L;

	private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

	private static final MediaType EXCEL_MEDIA_TYPE = MediaType.parseMediaType(
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

	private final ReporteVencimientoProductoRepository repository;

	private final UserEmpresaService userEmpresaService;

	private final EmpresaService empresaService;

	private final MessageSource messageSource;

	private final DataSource dataSource;

	@Value("${path.logos}")
	private String pathLogos;

	@Value("${path.logo.empresa}")
	private String pathLogoCompany;

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
	public ReporteVencimientoProductoArchivo exportar(
			ReporteVencimientoProductoFiltroDTO filtro,
			ReporteVencimientoProductoFormato formato,
			Locale locale) {
		ReporteVencimientoProductoFiltroDTO normalized = normalize(filtro);
		validate(normalized);

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		LocalDate fechaGeneracion = LocalDate.now();
		ReporteVencimientoProductoFormato formatoNormalizado = formato == null
				? ReporteVencimientoProductoFormato.PDF
				: formato;
		try {
			List<ResultadoVencimientoRow> rows = repository.findResultados(empresaId, normalized, fechaGeneracion);
			if (rows.isEmpty()) {
				throw new ReporteVencimientoProductoException(
						"report.vencimiento.no-results.export",
						HttpStatus.NOT_FOUND);
			}

			Map<String, Object> parameters = buildReportParameters(empresaId, normalized, fechaGeneracion, locale);
			JasperReport jasperReport = compileReport();
			JasperPrint jasperPrint = fillReport(jasperReport, parameters);
			byte[] content = switch (formatoNormalizado) {
				case EXCEL -> exportXlsx(jasperPrint);
				case PDF -> JasperExportManager.exportReportToPdf(jasperPrint);
			};
			return new ReporteVencimientoProductoArchivo(
					content,
					buildFileName(formatoNormalizado, fechaGeneracion),
					formatoNormalizado.mediaType());
		}
		catch (ReporteVencimientoProductoException exception) {
			throw exception;
		}
		catch (JRException | SQLException | DataAccessException | IllegalStateException exception) {
			log.error("No fue posible generar el reporte de vencimiento para la empresa {}", empresaId, exception);
			throw new ReporteVencimientoProductoException(
					"report.vencimiento.export.error",
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

	String buildCondicion(
			Long empresaId,
			ReporteVencimientoProductoFiltroDTO filtro,
			LocalDate fechaGeneracion) {
		StringBuilder condition = new StringBuilder();
		condition.append("ki.kai_empresa_id = ").append(empresaId);
		condition.append(" AND ki.kai_estado_id = ").append(ESTADO_ACTIVO);
		condition.append(" AND k.kar_estado_id = ").append(ESTADO_ACTIVO);
		condition.append(" AND pp.prp_estado_id = ").append(ESTADO_ACTIVO);
		condition.append(" AND p.pro_estado_id = ").append(ESTADO_ACTIVO);
		condition.append(" AND pc.prc_estado_id = ").append(ESTADO_ACTIVO);
		condition.append(" AND a.alm_estado_id = ").append(ESTADO_ACTIVO);
		condition.append(" AND e.esp_estado_id = ").append(ESTADO_ACTIVO);
		condition.append(" AND b.blo_estado_id = ").append(ESTADO_ACTIVO);
		condition.append(" AND s.sed_estado_id = ").append(ESTADO_ACTIVO);
		condition.append(" AND ki.kai_fecha_vencimiento IS NOT NULL");
		condition.append(" AND ki.kai_fecha_vencimiento BETWEEN DATE '")
			.append(filtro.fechaInicio())
			.append("' AND DATE '")
			.append(filtro.fechaFin())
			.append("'");

		appendLongCondition(condition, "pa.pai_id", filtro.paisId());
		appendLongCondition(condition, "d.dep_id", filtro.departamentoId());
		appendLongCondition(condition, "m.mun_id", filtro.municipioId());
		appendLongCondition(condition, "s.sed_id", filtro.sedeId());
		appendLongCondition(condition, "b.blo_id", filtro.bloqueId());
		appendLongCondition(condition, "e.esp_id", filtro.espacioId());
		appendLongCondition(condition, "a.alm_id", filtro.almacenId());
		appendLongCondition(condition, "pc.prc_id", filtro.categoriaId());
		appendLongCondition(condition, "p.pro_id", filtro.productoId());
		appendLongCondition(condition, "pp.prp_id", filtro.presentacionId());

		if (filtro.estadoNormalizado() == ReporteVencimientoProductoEstado.VENCIDO) {
			condition.append(" AND ki.kai_fecha_vencimiento <= DATE '").append(fechaGeneracion).append("'");
		}
		if (filtro.estadoNormalizado() == ReporteVencimientoProductoEstado.PROXIMO_A_VENCER) {
			condition.append(" AND ki.kai_fecha_vencimiento > DATE '").append(fechaGeneracion).append("'");
		}

		return condition.toString();
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

	private Map<String, Object> buildReportParameters(
			Long empresaId,
			ReporteVencimientoProductoFiltroDTO filtro,
			LocalDate fechaGeneracion,
			Locale locale) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("condicion", buildCondicion(empresaId, filtro, fechaGeneracion));
		parameters.put("logo_empresa", resolveLogo(empresaId));
		parameters.put("report_title", message("report.vencimiento.title", null, locale));
		parameters.put("label_fecha_generacion", message("report.vencimiento.generated-at", null, locale));
		parameters.put("label_empresa", message("report.vencimiento.label.empresa", null, locale));
		parameters.put("label_sede", message("report.vencimiento.label.sede", null, locale));
		parameters.put("label_bloque", message("report.vencimiento.label.bloque", null, locale));
		parameters.put("label_espacio", message("report.vencimiento.label.espacio", null, locale));
		parameters.put("label_almacen", message("report.vencimiento.label.almacen", null, locale));
		parameters.put("label_municipio", message("report.vencimiento.label.municipio", null, locale));
		parameters.put("label_producto", message("report.vencimiento.column.producto", null, locale));
		parameters.put("label_estado", message("report.vencimiento.column.estado", null, locale));
		parameters.put("label_fecha_vencimiento", message("report.vencimiento.column.fecha-vencimiento", null, locale));
		parameters.put("label_pagina", message("report.vencimiento.page", null, locale));
		parameters.put("estado_vencido", message("report.vencimiento.estado.vencido", null, locale));
		parameters.put("estado_proximo", message("report.vencimiento.estado.proximo", null, locale));
		parameters.put("fecha_generacion", java.sql.Date.valueOf(fechaGeneracion));
		return parameters;
	}

	private JasperReport compileReport() throws JRException {
		try (InputStream stream = getClass().getClassLoader().getResourceAsStream(REPORTE_RUTA)) {
			if (stream == null) {
				throw new IllegalStateException("No se encontro: " + REPORTE_RUTA);
			}
			return JasperCompileManager.compileReport(stream);
		}
		catch (java.io.IOException exception) {
			throw new JRException(exception);
		}
	}

	private JasperPrint fillReport(JasperReport jasperReport, Map<String, Object> parameters)
			throws SQLException, JRException {
		try (Connection connection = dataSource.getConnection()) {
			return JasperFillManager.fillReport(jasperReport, parameters, connection);
		}
	}

	private byte[] exportXlsx(JasperPrint jasperPrint) throws JRException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JRXlsxExporter exporter = new JRXlsxExporter();
		SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
		configuration.setOnePagePerSheet(false);
		configuration.setDetectCellType(true);
		configuration.setCollapseRowSpan(false);
		configuration.setWhitePageBackground(false);
		configuration.setRemoveEmptySpaceBetweenRows(true);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return outputStream.toByteArray();
	}

	private String resolveLogo(Long empresaId) {
		String logoHash = empresaService.getLogoHashByEmpresaId(empresaId);
		if (logoHash == null || logoHash.isBlank()) {
			return "";
		}
		String logoFileName = empresaService.findLogoByHash(logoHash);
		if (logoFileName == null || logoFileName.isBlank()) {
			return "";
		}
		Path logoPath = Paths.get(pathLogos, pathLogoCompany, empresaId.toString(), logoFileName);
		return logoPath.toString();
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

	private void appendLongCondition(StringBuilder condition, String column, Long value) {
		if (value != null) {
			condition.append(" AND ").append(column).append(" = ").append(value);
		}
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

	private String buildFileName(ReporteVencimientoProductoFormato formato, LocalDate fechaGeneracion) {
		return "vencimiento-producto-" + FILE_DATE_FORMAT.format(fechaGeneracion) + formato.extension();
	}

	public enum ReporteVencimientoProductoFormato {

		PDF(".pdf", MediaType.APPLICATION_PDF),

		EXCEL(".xlsx", EXCEL_MEDIA_TYPE);

		private final String extension;

		private final MediaType mediaType;

		ReporteVencimientoProductoFormato(String extension, MediaType mediaType) {
			this.extension = extension;
			this.mediaType = mediaType;
		}

		public static ReporteVencimientoProductoFormato parse(String raw) {
			if (raw == null || raw.isBlank()) {
				return PDF;
			}
			for (ReporteVencimientoProductoFormato formato : values()) {
				if (formato.name().equalsIgnoreCase(raw)) {
					return formato;
				}
			}
			throw new ReporteVencimientoProductoException(
					"report.vencimiento.format.invalid",
					HttpStatus.BAD_REQUEST);
		}

		public String extension() {
			return extension;
		}

		public MediaType mediaType() {
			return mediaType;
		}

	}

	public record ReporteVencimientoProductoArchivo(
			byte[] contenido,
			String nombreArchivo,
			MediaType mediaType) {
	}

}
