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
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            for (Map.Entry<String, Object> entry : parametros.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof String string
                        && (key.toLowerCase().contains("fecha") || key.toLowerCase().contains("date"))) {
                    try {
                        Date parsedDate = sdf.parse(string);
                        parametros.put(key, parsedDate);
                    } catch (ParseException e) {
                        throw new RuntimeException("Error al parsear la fecha para el parámetro: " + key, e);
                    }
                }
            }
            Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
            System.out.println("Empresa actual: " + empresaId); 
            parametros.put("emp_id", empresaId.intValue());

            String empLogoHash = empresaService.getLogoHashByEmpresaId(empresaId);
            System.out.println("buscando hash "+ empLogoHash);

            String empLogo = empresaService.findLogoByHash(empLogoHash);
            System.out.println("emplogo buscado "+ empLogo);

            Path rutaLogo = Paths.get(pathLogos, pathLogoCompany, empresaId.toString(), empLogo);
            System.out.println("Path buscado "+ rutaLogo);

            if (Files.exists(rutaLogo)) {
                parametros.put("logo_empresa", rutaLogo.toString());
            } else {
                parametros.put("logo_empresa", "https://static.vecteezy.com/system/resources/thumbnails/012/986/755/small/abstract-circle-logo-icon-free-png.png");
            }
            InputStream reportStream = getClass().getResourceAsStream("/reports/" + reportName + ".jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            try (Connection connection = dataSource.getConnection()) {
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);
                return JasperExportManager.exportReportToPdf(jasperPrint);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
        }
    }

}
