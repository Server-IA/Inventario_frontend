package com.coagronet.reports.services;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.sql.DataSource;

import com.coagronet.empresa.services.EmpresaService;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.stereotype.Service;

@DependsOnDatabaseInitialization
@Service
@RequiredArgsConstructor
public class ReportService {

    private final DataSource dataSource;

    private final UserEmpresaService userEmpresaService;
    private final EmpresaService empresaService;

    @Value("${PATH_LOGOS}")
    private String pathLogos;

    @Value("${PATH_LOGO_COMPANY}")
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
            String empLogoHash = empresaService.getLogoHashByEmpresaId(empresaId);
            String empLogo = empresaService.findLogoByHash(empLogoHash);

            Path rutaLogo = Paths.get(pathLogos, pathLogoCompany, empresaId.toString(), empLogo);

            if (Files.exists(rutaLogo)) {
                parametros.put("header_empresa_logo", rutaLogo.toUri().toURL().toString());
            } else {
                parametros.put("header_empresa_logo", "https://ruta-del-logo-por-defecto.png");
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
