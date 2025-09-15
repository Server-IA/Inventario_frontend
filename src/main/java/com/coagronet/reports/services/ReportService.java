package com.coagronet.reports.services;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.stereotype.Service;

import com.coagronet.empresa.services.EmpresaService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@DependsOnDatabaseInitialization
@Service
@RequiredArgsConstructor
public class ReportService {

	private final DataSource dataSource;

	private final UserEmpresaService userEmpresaService;

	private final EmpresaService empresaService;

	@Value("${path.logos}")
	private String pathLogos;

	@Value("${path.logo.empresa}")
	private String pathLogoCompany;

	public byte[] generarReporte(String nombreReporte, Map<String, Object> parametros) {

		long inicio = System.currentTimeMillis();
		try {
			procesarFechas(parametros);
			Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
			System.out.println("Empresa actual: " + empresaId);

			parametros.put("empresa_id", empresaId.intValue());

			agregarLogo(parametros, empresaId);

			JasperReport jasperReport = compilarReporte(nombreReporte);
			return exportarPdf(jasperReport, parametros, inicio, nombreReporte);

		}
		catch (Exception e) {
			throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
		}
	}

	private void procesarFechas(Map<String,Object> parametros) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		for (Map.Entry<String, Object> entry : parametros.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (value instanceof String string
					&& (key.toLowerCase().contains("fecha") || key.toLowerCase().contains("date"))) {
				try {
					Date parsedDate = sdf.parse(string);
					parametros.put(key, new java.sql.Timestamp(parsedDate.getTime()));
				}
				catch (ParseException e) {
					System.err.println("ERROR: Fallo al parsear fecha para el parámetro: " + key);
					throw new RuntimeException("Error al parsear la fecha para el parámetro: " + key, e);
				}
			}
		}
	}

	private void agregarLogo(Map<String, Object> parametros, Long empresaId){
		final String LOGO_POR_DEFECTO = "https://static.vecteezy.com/system/resources/thumbnails/012/986/755/small/abstract-circle-logo-icon-free-png.png";

		String logoHash = empresaService.getLogoHashByEmpresaId(empresaId);
		String  empLogoNombre = empresaService.findLogoByHash(logoHash);

		if (empLogoNombre == null || empLogoNombre.isBlank()) {
			parametros.put("logo_empresa", LOGO_POR_DEFECTO);
			return;
		}

		Path rutaLogo = Paths.get(pathLogos, pathLogoCompany, empresaId.toString(), empLogoNombre);
		System.out.println("Ruta de logo construida: " + rutaLogo);

		parametros.put("logo_empresa", rutaLogo.toString());
	}

	private JasperReport compilarReporte(String nombre)throws Exception{
		String rutaReporte = "reports/"+nombre+".jrxml";
		try (InputStream stream = getClass().getClassLoader().getResourceAsStream(rutaReporte)){
			if (stream == null) throw new IllegalStateException("No se encontró: "+ rutaReporte);
			return JasperCompileManager.compileReport(stream);
		}
	}

	private byte[] exportarPdf(JasperReport reporte, Map<String, Object> parametros, long inicio, String nombreReporte) throws Exception{
		try (Connection connection = dataSource.getConnection()) {
			JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, connection);
			byte[] reportePdf = JasperExportManager.exportReportToPdf(jasperPrint);
			long fin = System.currentTimeMillis();
			System.out.println("Reporte " + nombreReporte + " generado en " + (fin-inicio) + " ms.");
			return reportePdf;
		}

	}


	private void procesarCondicion(Map<String, Object> parametros, Long empresaId) {
		Object raw = parametros.get("condicion");
		if (raw == null) {
			parametros.put("CONDICION", ""); // sin filtro extra
			return;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(raw.toString());

			// Ordenar por clave numérica y concatenar
			StringBuilder sb = new StringBuilder();
			root.fieldNames().forEachRemaining(field -> {
			}); // para obtener las claves

			root.fieldNames().forEachRemaining(field -> {
			}); // nada, solo para iterar

			// Usamos un stream para ordenar las claves numéricamente
			root.fieldNames().forEachRemaining(k -> {
			});
			java.util.List<String> keys = new java.util.ArrayList<>();
			root.fieldNames().forEachRemaining(keys::add);
			keys.sort(java.util.Comparator.comparingInt(Integer::parseInt));

			for (String key : keys) {
				String fragment = root.get(key).asText();

				// Validación básica: evita inyección
				if (!fragment.matches("[\\w\\s=.<>'\"$%-_]+")) {
					throw new IllegalArgumentException("Condición inválida en clave " + key);
				}

				// Reemplazar variable especial
				fragment = fragment.replace("$EMPRESA_ID$", empresaId.toString());
				sb.append(' ').append(fragment);
			}

			parametros.put("CONDICION", sb.toString().trim());
		} catch (Exception e) {
			throw new RuntimeException("Error al procesar el filtro de condición", e);
		}

	}

	public byte[] generarReporteNuevo(String nombreReporte, Map<String, Object> parametros) {

		long inicio = System.currentTimeMillis();
		try {
			procesarFechas(parametros);
			Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
			System.out.println("Empresa actual: " + empresaId);

			parametros.put("empresa_id", empresaId.intValue());

			procesarCondicion(parametros, empresaId);

			agregarLogo(parametros, empresaId);

			JasperReport jasperReport = compilarReporte(nombreReporte);
			return exportarPdf(jasperReport, parametros, inicio, nombreReporte);

		} catch (Exception e) {
			throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
		}
	}
}
