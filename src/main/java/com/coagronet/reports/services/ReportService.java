package com.coagronet.reports.services;

import java.io.InputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.stereotype.Service;

@DependsOnDatabaseInitialization
@Service
@RequiredArgsConstructor
public class ReportService {

    @Autowired
    private DataSource dataSource;

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
                        parametros.put(key, new java.sql.Timestamp(parsedDate.getTime()));
                    } catch (ParseException e) {
                        throw new RuntimeException("Error al parsear la fecha para el parámetro: " + key, e);
                    }
                }
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
