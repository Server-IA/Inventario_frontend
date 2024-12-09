package com.coagronet.reports.services;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class ReportService {

    @Autowired
    private DataSource dataSource;

    public byte[] generateProductoReport(int category) throws Exception {
        // Load the compiled JasperReport file (.jasper)
        JasperReport jasperReport;
        try (InputStream reportStream = new ClassPathResource("producto4.jrxml").getInputStream()) {
            jasperReport = JasperCompileManager.compileReport(reportStream);
        } catch (Exception e) {
            System.out.println("Error durante la compilación del reporte: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to compile report.", e);
        }

        // Ensure that your SQL query returns data from the database
        try (Connection connection = dataSource.getConnection()) {
            // Parameters (if any)
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportTitle", "Products Report");

            String queryCategory = "";
            if (category > 0) {
                queryCategory = "" + category;
            }
            parameters.put("QueryCategory", queryCategory);

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    connection);

            System.out.println("R91");

            // Check if the report was filled with data
            if (jasperPrint.getPages().isEmpty()) {
                throw new RuntimeException("No data found for the report.");
            }

            // Export the report to PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            System.out.println("Error durante la generación del reporte: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate report.", e);
        }
    }

    public byte[] generatePedidoReport(Integer id) throws Exception {
        // Load the compiled JasperReport file (.jasper)
        JasperReport jasperReport;
        try (InputStream reportStream = new ClassPathResource("Reporte_pedido.jrxml").getInputStream()) {
            jasperReport = JasperCompileManager.compileReport(reportStream);
        } catch (Exception e) {
            System.out.println("Error durante la compilación del reporte: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to compile report.", e);
        }

        // Ensure that your SQL query returns data from the database
        try (Connection connection = dataSource.getConnection()) {
            // Parameters (if any)
            Map<String, Object> parameters = new HashMap<>();

            Integer ped_id = null;
            if (id > 0) {
                ped_id = id;
            }
            parameters.put("ped_id", ped_id);

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    connection);

            System.out.println("R91");

            // Check if the report was filled with data
            if (jasperPrint.getPages().isEmpty()) {
                throw new RuntimeException("No data found for the report.");
            }

            // Export the report to PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            System.out.println("Error durante la generación del reporte: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate report.", e);
        }
    }

    public byte[] generateOrdenCompraReport(Integer id) throws Exception {
        // Load the compiled JasperReport file (.jasper)
        JasperReport jasperReport;
        try (InputStream reportStream = new ClassPathResource("Reporte_oden_compra.jrxml").getInputStream()) {
            jasperReport = JasperCompileManager.compileReport(reportStream);
        } catch (Exception e) {
            System.out.println("Error durante la compilación del reporte: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to compile report.", e);
        }

        // Ensure that your SQL query returns data from the database
        try (Connection connection = dataSource.getConnection()) {
            // Parameters (if any)
            Map<String, Object> parameters = new HashMap<>();

            Integer orc_id = null;
            if (id > 0) {
                orc_id = id;
            }
            parameters.put("orc_id", orc_id);

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    connection);

            System.out.println("R91");

            // Check if the report was filled with data
            if (jasperPrint.getPages().isEmpty()) {
                throw new RuntimeException("No data found for the report.");
            }

            // Export the report to PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            System.out.println("Error durante la generación del reporte: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate report.", e);
        }
    }
}
