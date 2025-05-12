package com.coagronet.reports.services;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    @Autowired
    private DataSource dataSource;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public byte[] generarReporte(String tableName, Map<String, Object> parametros) {
        try {
            InputStream reportStream = getClass().getResourceAsStream("/reports/" + tableName + ".jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            JRDataSource dataSource = new JREmptyDataSource();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
        }
    }

    public byte[] generarReporteSQL(String tableName, Map<String, Object> parametros) {
        try {
            InputStream reportStream = getClass().getResourceAsStream("/reports/" + tableName + ".jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Connection connection = dataSource.getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);


            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
        }
    }







    private byte[] generateReport(String reportPath, Map<String, Object> parameters) throws Exception {
        JasperReport jasperReport;
        try (InputStream reportStream = new ClassPathResource(reportPath).getInputStream()) {
            jasperReport = JasperCompileManager.compileReport(reportStream);
        } catch (Exception e) {
            System.out.println("Error durante la compilación del reporte: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to compile report.", e);
        }

        try (Connection connection = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            if (jasperPrint.getPages().isEmpty()) {
                throw new RuntimeException("No data found for the report.");
            }

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            System.out.println("Error durante la generación del reporte: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate report.", e);
        }
    }




    public byte[] generateProductoReport(int category) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Products Report");
        parameters.put("QueryCategory", category > 0 ? String.valueOf(category) : "");

        return generateReport("producto4.jrxml", parameters);
    }

    public byte[] generatePedidoReport(Integer id) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ped_id", id > 0 ? id : null);

        return generateReport("Reporte_pedido.jrxml", parameters);
    }

    public byte[] generateOrdenCompraReport(Integer id) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orc_id", id > 0 ? id : null);

        return generateReport("Reporte_oden_compra.jrxml", parameters);
    }
    
}
