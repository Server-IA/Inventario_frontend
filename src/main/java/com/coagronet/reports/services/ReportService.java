package com.coagronet.reports.services;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

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

	public byte[] generarReporte(String reportName, Map<String, Object> parametros) {
		System.out.println("--- INICIANDO GENERACION DE REPORTE: " + reportName + " ---");
		long startTime = System.currentTimeMillis();

		try {
			System.out.println("[1/9] Procesando parámetros de entrada...");
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
			System.out.println("[2/9] Obteniendo ID de empresa...");
			Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
			System.out.println("Empresa actual: " + empresaId);
			parametros.put("empresa_id", empresaId.intValue());

			System.out.println("[3/9] Buscando logo de la empresa...");
			String empLogoHash = empresaService.getLogoHashByEmpresaId(empresaId);
			String empLogo = empresaService.findLogoByHash(empLogoHash);
			Path rutaLogo = Paths.get(pathLogos, pathLogoCompany, empresaId.toString(), empLogo);
			System.out.println("Ruta de logo construida: " + rutaLogo);

			if (Files.exists(rutaLogo)) {
				parametros.put("logo_empresa", rutaLogo.toString());
				System.out.println("Logo encontrado en: " + rutaLogo);
			}
			else {
				parametros.put("logo_empresa",
						"https://static.vecteezy.com/system/resources/thumbnails/012/986/755/small/abstract-circle-logo-icon-free-png.png");
				System.out.println("Logo no encontrado, usando logo por defecto.");
			}

			System.out.println("[4/9] Cargando plantilla de reporte (.jrxml)...");
			String reportPath = "reports/" + reportName + ".jrxml";
			InputStream reportStream = getClass().getClassLoader().getResourceAsStream(reportPath);

			if (reportStream == null) {
				System.err.println("ERROR: No se pudo encontrar el archivo de reporte en el classpath: " + reportPath);
				throw new RuntimeException("No se pudo encontrar el archivo de reporte: " + reportPath);
			}
			System.out.println("Plantilla encontrada, procediendo a compilar...");

			System.out.println("[5/9] Compilando reporte...");
			JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
			System.out.println("Reporte compilado exitosamente.");

			System.out.println("[6/9] Obteniendo conexión a la base de datos...");
			try (Connection connection = dataSource.getConnection()) {
				System.out.println("[7/9] Conexión obtenida, llenando el reporte...");
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);
				System.out.println("[8/9] Reporte llenado, exportando a PDF...");
				byte[] reportePdf = JasperExportManager.exportReportToPdf(jasperPrint);
				long endTime = System.currentTimeMillis();
				System.out.println("[9/9] Reporte exportado a PDF exitosamente. Duración total: " + (endTime - startTime) + " ms.");
				System.out.println("--- FINALIZADO GENERACION DE REPORTE: " + reportName + " ---");
				return reportePdf;
			}

		}
		catch (Exception e) {
			long endTime = System.currentTimeMillis();
			System.err.println("--- ERROR FATAL DURANTE LA GENERACION DEL REPORTE ---");
			System.err.println("Reporte: " + reportName);
			System.err.println("Duración hasta el error: " + (endTime - startTime) + " ms.");
			e.printStackTrace(); // Imprimir toda la traza del error
			throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
		}
	}

}
