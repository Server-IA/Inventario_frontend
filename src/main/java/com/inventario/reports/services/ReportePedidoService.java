/*=============================================================================
 Nombre del archivo : ReportePedidoService.java
 Descripcion        : Servicio para consultar, generar y exportar reportes de pedido.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-18 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.auditoria.AuthenticationService;
import com.inventario.reports.dtos.ReporteKardexFiltroOpcionDTO;
import com.inventario.reports.dtos.ReporteKardexSeleccionInicialDTO;
import com.inventario.reports.dtos.ReportePedidoConsultaResponseDTO;
import com.inventario.reports.dtos.ReportePedidoFiltroDTO;
import com.inventario.reports.dtos.ReportePedidoJasperRowDTO;
import com.inventario.reports.dtos.ReportePedidoPreloadDTO;
import com.inventario.reports.dtos.ReportePedidoResumenItemDTO;
import com.inventario.reports.exceptions.ReportePedidoException;
import com.inventario.reports.repositories.ReportePedidoAuditoriaRepository;
import com.inventario.reports.repositories.ReportePedidoRepository;
import com.inventario.reports.repositories.ReportePedidoRepository.PedidoReporteRow;
import com.inventario.reports.repositories.ReportePedidoRepository.UbicacionPedidoRow;
import com.inventario.user.User;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReportePedidoService {

	private static final String REPORTE_RUTA = "reports/pedido.jrxml";

	private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

	private static final MediaType EXCEL_MEDIA_TYPE = MediaType.parseMediaType(
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

	private final ReportePedidoRepository repository;
	private final ReportePedidoAuditoriaRepository auditoriaRepository;
	private final UserEmpresaService userEmpresaService;
	private final AuthenticationService authenticationService;
	private final MessageSource messageSource;

	@Value("${path.logos}")
	private String pathLogos;

	@Value("${path.logo.empresa}")
	private String pathLogoCompany;

	@Transactional(readOnly = true)
	public ReportePedidoPreloadDTO preload() {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		try {
			List<UbicacionPedidoRow> ubicaciones = repository.findUbicaciones(empresaId);
			List<ReporteKardexFiltroOpcionDTO> paises = distinctOptions(
					ubicaciones, UbicacionPedidoRow::paisId, UbicacionPedidoRow::pais, row -> null);
			List<ReporteKardexFiltroOpcionDTO> departamentos = distinctOptions(
					ubicaciones, UbicacionPedidoRow::departamentoId, UbicacionPedidoRow::departamento,
					UbicacionPedidoRow::paisId);
			List<ReporteKardexFiltroOpcionDTO> municipios = distinctOptions(
					ubicaciones, UbicacionPedidoRow::municipioId, UbicacionPedidoRow::municipio,
					UbicacionPedidoRow::departamentoId);
			List<ReporteKardexFiltroOpcionDTO> sedes = distinctOptions(
					ubicaciones, UbicacionPedidoRow::sedeId, UbicacionPedidoRow::sede,
					UbicacionPedidoRow::municipioId);
			List<ReporteKardexFiltroOpcionDTO> bloques = distinctOptions(
					ubicaciones, UbicacionPedidoRow::bloqueId, UbicacionPedidoRow::bloque,
					UbicacionPedidoRow::sedeId);
			List<ReporteKardexFiltroOpcionDTO> espacios = distinctOptions(
					ubicaciones, UbicacionPedidoRow::espacioId, UbicacionPedidoRow::espacio,
					UbicacionPedidoRow::bloqueId);
			List<ReporteKardexFiltroOpcionDTO> almacenes = distinctOptions(
					ubicaciones, UbicacionPedidoRow::almacenId, UbicacionPedidoRow::almacen,
					UbicacionPedidoRow::espacioId);
			LocalDate today = LocalDate.now();
			return new ReportePedidoPreloadDTO(
					today.withDayOfMonth(1),
					today,
					!ubicaciones.isEmpty(),
					repository.findPedidos(empresaId),
					repository.findEstados(empresaId),
					paises,
					departamentos,
					municipios,
					sedes,
					bloques,
					espacios,
					almacenes,
					initialSelection(paises, departamentos, municipios, sedes, bloques, espacios, almacenes));
		}
		catch (DataAccessException exception) {
			log.error("No fue posible precargar los filtros de pedido para la empresa {}", empresaId, exception);
			throw new ReportePedidoException(
					"report.pedido.preload.error", HttpStatus.INTERNAL_SERVER_ERROR, exception);
		}
	}

	@Transactional(readOnly = true)
	public ReportePedidoConsultaResponseDTO resumen(ReportePedidoFiltroDTO filtro, Locale locale) {
		ReportePedidoFiltroDTO normalized = normalizeAndValidate(filtro);
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		try {
			List<PedidoAggregate> pedidos = aggregate(repository.findResultados(empresaId, normalized));
			List<ReportePedidoResumenItemDTO> summaries = pedidos.stream()
				.map(pedido -> toSummary(pedido, locale))
				.toList();
			String messageKey = summaries.isEmpty() ? "report.pedido.no-results" : "report.pedido.results";
			return new ReportePedidoConsultaResponseDTO(
					LocalDateTime.now(),
					summaries.size(),
					message(messageKey, new Object[] { summaries.size() }, locale),
					summaries);
		}
		catch (DataAccessException exception) {
			log.error("No fue posible consultar el resumen de pedidos para la empresa {}", empresaId, exception);
			throw new ReportePedidoException(
					"report.pedido.search.error", HttpStatus.INTERNAL_SERVER_ERROR, exception);
		}
	}

	@Transactional
	public ReportePedidoArchivo exportar(
			ReportePedidoFiltroDTO filtro,
			ReportePedidoFormato formato,
			Locale locale) {
		ReportePedidoFiltroDTO normalized = normalizeAndValidate(filtro);
		ReportePedidoFormato normalizedFormat = formato == null ? ReportePedidoFormato.PDF : formato;
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		LocalDateTime generatedAt = LocalDateTime.now();
		try {
			List<PedidoAggregate> pedidos = aggregate(repository.findResultados(empresaId, normalized));
			if (pedidos.isEmpty()) {
				throw new ReportePedidoException("report.pedido.no-results.export", HttpStatus.NOT_FOUND);
			}

			List<ReportePedidoJasperRowDTO> data = toJasperRows(pedidos, locale);
			Map<String, Object> parameters = buildReportParameters(
					empresaId, pedidos.getFirst().header().logoArchivo(), generatedAt, locale);
			JasperReport report = compileReport();
			JasperPrint print = JasperFillManager.fillReport(
					report, parameters, new JRBeanCollectionDataSource(data));
			byte[] content = switch (normalizedFormat) {
				case PDF -> JasperExportManager.exportReportToPdf(print);
				case EXCEL -> exportXlsx(print);
			};

			User user = authenticationService.getAuthenticatedUser();
			List<Long> pedidoIds = pedidos.stream().map(pedido -> pedido.header().pedidoId()).toList();
			auditoriaRepository.registrar(
					UUID.randomUUID(),
					empresaId,
					user.getId(),
					user.getUsername(),
					OffsetDateTime.now(ZoneId.systemDefault()),
					pedidoIds,
					normalizedFormat.name());

			log.info("Reporte de pedido generado. empresaId={}, usuarioId={}, pedidos={}, formato={}",
					empresaId, user.getId(), pedidoIds, normalizedFormat);
			return new ReportePedidoArchivo(
					content,
					buildFileName(normalizedFormat, generatedAt),
					normalizedFormat.mediaType());
		}
		catch (ReportePedidoException exception) {
			throw exception;
		}
		catch (JRException exception) {
			log.error("No fue posible generar el reporte de pedido para la empresa {}", empresaId, exception);
			throw new ReportePedidoException(
					"report.pedido.export.error", HttpStatus.INTERNAL_SERVER_ERROR, exception);
		}
		catch (RuntimeException exception) {
			log.error("No fue posible generar el reporte de pedido para la empresa {}", empresaId, exception);
			throw new ReportePedidoException(
					"report.pedido.export.error", HttpStatus.INTERNAL_SERVER_ERROR, exception);
		}
	}

	ReportePedidoFiltroDTO normalizeAndValidate(ReportePedidoFiltroDTO filtro) {
		if (filtro == null) {
			throw new ReportePedidoException("report.pedido.request.required", HttpStatus.BAD_REQUEST);
		}
		List<Long> pedidoIds = filtro.pedidoIds() == null
				? List.of()
				: filtro.pedidoIds().stream()
					.filter(Objects::nonNull)
					.filter(id -> id > 0)
					.distinct()
					.toList();
		if (pedidoIds.size() > 500) {
			throw new ReportePedidoException("report.pedido.pedidos.max", HttpStatus.BAD_REQUEST);
		}
		if (filtro.fechaInicio() != null && filtro.fechaFin() != null
				&& filtro.fechaInicio().isAfter(filtro.fechaFin())) {
			throw new ReportePedidoException("report.pedido.date-range.invalid", HttpStatus.BAD_REQUEST);
		}
		return new ReportePedidoFiltroDTO(
				pedidoIds,
				positiveOrNull(filtro.estadoId()),
				filtro.fechaInicio(),
				filtro.fechaFin(),
				positiveOrNull(filtro.paisId()),
				positiveOrNull(filtro.departamentoId()),
				positiveOrNull(filtro.municipioId()),
				positiveOrNull(filtro.sedeId()),
				positiveOrNull(filtro.bloqueId()),
				positiveOrNull(filtro.espacioId()),
				positiveOrNull(filtro.almacenId()));
	}

	List<PedidoAggregate> aggregate(List<PedidoReporteRow> rows) {
		Map<Long, List<PedidoReporteRow>> grouped = new LinkedHashMap<>();
		for (PedidoReporteRow row : rows) {
			grouped.computeIfAbsent(row.pedidoId(), ignored -> new ArrayList<>()).add(row);
		}
		return grouped.values().stream()
			.map(group -> new PedidoAggregate(group.getFirst(), group))
			.toList();
	}

	private ReportePedidoResumenItemDTO toSummary(PedidoAggregate pedido, Locale locale) {
		List<PedidoReporteRow> items = pedido.items();
		int productCount = (int) items.stream().filter(this::hasItem).map(PedidoReporteRow::pedidoItemId).distinct().count();
		boolean empty = productCount == 0;
		boolean missingUnit = items.stream().filter(this::hasItem).anyMatch(row -> isBlank(row.unidad()));
		BigDecimal total = missingUnit
				? BigDecimal.ZERO
				: items.stream()
					.filter(this::hasItem)
					.map(row -> row.cantidad() == null ? BigDecimal.ZERO : row.cantidad())
					.reduce(BigDecimal.ZERO, BigDecimal::add);
		String warning = warning(empty, missingUnit, locale);
		PedidoReporteRow header = pedido.header();
		return new ReportePedidoResumenItemDTO(
				header.pedidoId(),
				header.pedidoFecha(),
				header.pedidoEstado(),
				header.almacen(),
				productCount,
				total,
				empty,
				missingUnit,
				warning);
	}

	private List<ReportePedidoJasperRowDTO> toJasperRows(List<PedidoAggregate> pedidos, Locale locale) {
		List<ReportePedidoJasperRowDTO> result = new ArrayList<>();
		for (PedidoAggregate pedido : pedidos) {
			ReportePedidoResumenItemDTO summary = toSummary(pedido, locale);
			List<PedidoReporteRow> items = pedido.items().stream().filter(this::hasItem).toList();
			if (items.isEmpty()) {
				result.add(toJasperRow(pedido.header(), null, null, summary));
				continue;
			}
			for (int index = 0; index < items.size(); index++) {
				result.add(toJasperRow(pedido.header(), items.get(index), index + 1, summary));
			}
		}
		return result;
	}

	private ReportePedidoJasperRowDTO toJasperRow(
			PedidoReporteRow header,
			PedidoReporteRow item,
			Integer index,
			ReportePedidoResumenItemDTO summary) {
		return ReportePedidoJasperRowDTO.builder()
			.pedidoId(header.pedidoId())
			.fechaPedido(header.pedidoFecha())
			.estado(valueOrEmpty(header.pedidoEstado()))
			.empresa(valueOrEmpty(header.empresa()))
			.sede(valueOrEmpty(header.sede()))
			.bloque(valueOrEmpty(header.bloque()))
			.espacio(valueOrEmpty(header.espacio()))
			.almacen(valueOrEmpty(header.almacen()))
			.municipio(valueOrEmpty(header.municipio()))
			.responsable(valueOrEmpty(header.responsable()))
			.contacto(valueOrEmpty(header.contacto()))
			.correo(valueOrEmpty(header.correo()))
			.itemIndice(index)
			.presentacionId(item == null ? null : item.presentacionId())
			.producto(item == null ? "" : valueOrEmpty(item.producto()))
			.cantidad(item == null || item.cantidad() == null ? BigDecimal.ZERO : item.cantidad())
			.unidad(item == null ? "" : valueOrEmpty(item.unidad()))
			.tieneProducto(item != null)
			.totalPedido(summary.totalCantidad())
			.advertencia(valueOrEmpty(summary.advertencia()))
			.build();
	}

	private Map<String, Object> buildReportParameters(
			Long empresaId,
			String logoFile,
			LocalDateTime generatedAt,
			Locale locale) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("logo_empresa", resolveLogo(empresaId, logoFile));
		parameters.put("fecha_generacion", generatedAt);
		parameters.put("report_title", message("report.pedido.title", null, locale));
		parameters.put("label_fecha_generacion", message("report.pedido.generated-at", null, locale));
		parameters.put("label_empresa", message("report.pedido.label.empresa", null, locale));
		parameters.put("label_estado", message("report.pedido.label.estado", null, locale));
		parameters.put("label_sede", message("report.pedido.label.sede", null, locale));
		parameters.put("label_bloque", message("report.pedido.label.bloque", null, locale));
		parameters.put("label_espacio", message("report.pedido.label.espacio", null, locale));
		parameters.put("label_almacen", message("report.pedido.label.almacen", null, locale));
		parameters.put("label_municipio", message("report.pedido.label.municipio", null, locale));
		parameters.put("label_responsable", message("report.pedido.label.responsable", null, locale));
		parameters.put("label_contacto", message("report.pedido.label.contacto", null, locale));
		parameters.put("label_correo", message("report.pedido.label.correo", null, locale));
		parameters.put("label_pedido_id", message("report.pedido.label.pedido-id", null, locale));
		parameters.put("label_fecha_pedido", message("report.pedido.label.fecha-pedido", null, locale));
		parameters.put("label_indice", message("report.pedido.label.indice", null, locale));
		parameters.put("label_presentacion_id", message("report.pedido.label.presentacion-id", null, locale));
		parameters.put("label_producto", message("report.pedido.label.producto", null, locale));
		parameters.put("label_cantidad", message("report.pedido.label.cantidad", null, locale));
		parameters.put("label_unidad", message("report.pedido.label.unidad", null, locale));
		parameters.put("label_total", message("report.pedido.label.total", null, locale));
		parameters.put("label_pagina", message("report.pedido.label.pagina", null, locale));
		return parameters;
	}

	private JasperReport compileReport() throws JRException {
		try (InputStream stream = getClass().getClassLoader().getResourceAsStream(REPORTE_RUTA)) {
			if (stream == null) {
				throw new IllegalStateException("No se encontro la plantilla del reporte de pedido");
			}
			return JasperCompileManager.compileReport(stream);
		}
		catch (java.io.IOException exception) {
			throw new IllegalStateException("No se pudo cerrar la plantilla del reporte", exception);
		}
	}

	private byte[] exportXlsx(JasperPrint print) throws JRException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setExporterInput(new SimpleExporterInput(print));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
			SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
			configuration.setOnePagePerSheet(true);
			configuration.setDetectCellType(true);
			configuration.setRemoveEmptySpaceBetweenRows(true);
			configuration.setWhitePageBackground(false);
			exporter.setConfiguration(configuration);
			exporter.exportReport();
			return output.toByteArray();
		}
		catch (java.io.IOException exception) {
			throw new IllegalStateException("No se pudo cerrar el archivo Excel", exception);
		}
	}

	private String resolveLogo(Long empresaId, String logoFile) {
		if (isBlank(logoFile)) {
			return "";
		}
		Path safeName = Paths.get(logoFile).getFileName();
		if (safeName == null) {
			return "";
		}
		Path logo = Paths.get(pathLogos, pathLogoCompany, empresaId.toString()).resolve(safeName).normalize();
		return Files.isRegularFile(logo) ? logo.toString() : "";
	}

	private String buildFileName(ReportePedidoFormato formato, LocalDateTime generatedAt) {
		return "reporte_pedido_" + FILE_DATE_FORMAT.format(generatedAt) + "." + formato.extension();
	}

	private String warning(boolean empty, boolean missingUnit, Locale locale) {
		if (empty) {
			return message("report.pedido.warning.empty", null, locale);
		}
		if (missingUnit) {
			return message("report.pedido.warning.unit-missing", null, locale);
		}
		return "";
	}

	private boolean hasItem(PedidoReporteRow row) {
		return row.pedidoItemId() != null;
	}

	private Long positiveOrNull(Long value) {
		return value != null && value > 0 ? value : null;
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	private String valueOrEmpty(String value) {
		return value == null ? "" : value;
	}

	private String message(String key, Object[] arguments, Locale locale) {
		return messageSource.getMessage(key, arguments, key, locale == null ? Locale.getDefault() : locale);
	}

	private <T> List<ReporteKardexFiltroOpcionDTO> distinctOptions(
			List<T> rows,
			Function<T, Long> idFunction,
			Function<T, String> nameFunction,
			Function<T, Long> parentFunction) {
		Map<Long, ReporteKardexFiltroOpcionDTO> options = new LinkedHashMap<>();
		for (T row : rows) {
			Long id = idFunction.apply(row);
			if (id != null) {
				options.putIfAbsent(id, new ReporteKardexFiltroOpcionDTO(
						id, valueOrEmpty(nameFunction.apply(row)), parentFunction.apply(row)));
			}
		}
		return options.values().stream()
			.sorted(Comparator.comparing(ReporteKardexFiltroOpcionDTO::nombre, String.CASE_INSENSITIVE_ORDER))
			.toList();
	}

	private ReporteKardexSeleccionInicialDTO initialSelection(
			List<ReporteKardexFiltroOpcionDTO> paises,
			List<ReporteKardexFiltroOpcionDTO> departamentos,
			List<ReporteKardexFiltroOpcionDTO> municipios,
			List<ReporteKardexFiltroOpcionDTO> sedes,
			List<ReporteKardexFiltroOpcionDTO> bloques,
			List<ReporteKardexFiltroOpcionDTO> espacios,
			List<ReporteKardexFiltroOpcionDTO> almacenes) {
		return new ReporteKardexSeleccionInicialDTO(
				onlyId(paises),
				onlyId(departamentos),
				onlyId(municipios),
				onlyId(sedes),
				onlyId(bloques),
				onlyId(espacios),
				onlyId(almacenes));
	}

	private Long onlyId(List<ReporteKardexFiltroOpcionDTO> options) {
		return options.size() == 1 ? options.getFirst().id() : null;
	}

	record PedidoAggregate(PedidoReporteRow header, List<PedidoReporteRow> items) {
	}

	public record ReportePedidoArchivo(byte[] contenido, String nombreArchivo, MediaType mediaType) {
	}

	public enum ReportePedidoFormato {
		PDF("pdf", MediaType.APPLICATION_PDF),
		EXCEL("xlsx", EXCEL_MEDIA_TYPE);

		private final String extension;
		private final MediaType mediaType;

		ReportePedidoFormato(String extension, MediaType mediaType) {
			this.extension = extension;
			this.mediaType = mediaType;
		}

		public String extension() {
			return extension;
		}

		public MediaType mediaType() {
			return mediaType;
		}

		public static ReportePedidoFormato parse(String value) {
			if (value == null || value.isBlank() || "PDF".equalsIgnoreCase(value)) {
				return PDF;
			}
			if ("EXCEL".equalsIgnoreCase(value) || "XLSX".equalsIgnoreCase(value)) {
				return EXCEL;
			}
			throw new ReportePedidoException("report.pedido.format.invalid", HttpStatus.BAD_REQUEST);
		}
	}

}
