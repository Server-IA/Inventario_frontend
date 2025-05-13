package com.coagronet.reports.controllers;

import net.sf.jasperreports.engine.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.coagronet.reports.services.ReportService;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/report")
@CrossOrigin(origins = "*")
public class ReportControllerV2 {

    private final ReportService reportService;
    private final DataSource dataSource;



    public ReportControllerV2(ReportService reportService, DataSource dataSource) {
        this.reportService = reportService;
        this.dataSource = dataSource;
    }

    @PostMapping("/test/{tableName}")
    public ResponseEntity<byte[]> generarReporte(
            @PathVariable String tableName,
            @RequestBody Map<String, Object> parametros) {

        byte[] reporte = reportService.generarReporte(tableName, parametros);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("reporte", tableName + ".pdf");

        return new ResponseEntity<>(reporte, headers, HttpStatus.OK);
    }

    @PostMapping("/test2/{reportName}")
    public ResponseEntity<byte[]> generarReporteSQL(@PathVariable String reportName,
                                                    @RequestBody Map<String, Object> parametros) {

        byte[] reporte = reportService.generarReporteSQL(reportName, parametros);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("reporte", reportName + ".pdf");

        return new ResponseEntity<>(reporte, headers, HttpStatus.OK);
    }






    private ResponseEntity<byte[]> generateReport(String reportName, byte[] reportData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", reportName + ".pdf");

        return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
    }


    @GetMapping("/producto")
    public ResponseEntity<byte[]> generateProductoReport(@RequestParam int category) {
        try {
            byte[] report = reportService.generateProductoReport(category);
            return generateReport("productoReport", report);
        } catch (Exception e) {
            System.out.println("Error LIA:" + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pedido")
    public ResponseEntity<byte[]> generatePedidoReport(@RequestParam Integer pedidoId) {
        try {
            byte[] report = reportService.generatePedidoReport(pedidoId);
            return generateReport("pedidoReport", report);
        } catch (Exception e) {
            System.out.println("Error LIA:" + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/orden_compra")
    public ResponseEntity<byte[]> generateOrdenCompraReport(@RequestParam Integer ordenId) {
        try {
            byte[] report = reportService.generateOrdenCompraReport(ordenId);
            return generateReport("ordenCompraReport", report);
        } catch (Exception e) {
            System.out.println("Error LIA:" + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
