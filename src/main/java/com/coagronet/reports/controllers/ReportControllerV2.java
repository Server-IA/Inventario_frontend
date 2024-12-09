package com.coagronet.reports.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.reports.services.ReportService;

@RestController
@RequestMapping("/api/v2/report")
@CrossOrigin(origins = "*")
public class ReportControllerV2 {

    private final ReportService reportService;

    public ReportControllerV2(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/producto")
    public ResponseEntity<byte[]> generateProductoReport(@RequestParam int category) {

        System.out.println("category=" + category);

        try {
            // Generate the report as a byte array
            byte[] report = reportService.generateProductoReport(category); // (message);

            // Set headers for PDF response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // headers.setContentDispositionFormData("attachment", "report.pdf");
            headers.setContentDispositionFormData("inline", "productoReport.pdf");
            // headers.setContentDispositionFormData("inline", "report.pdf");

            return new ResponseEntity<>(report, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("Error LIA:" + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pedido")
    public ResponseEntity<byte[]> generatePedidoReport(@RequestParam Integer pedidoId) {

        System.out.println("pedido=" + pedidoId);

        try {
            // Generate the report as a byte array
            byte[] report = reportService.generatePedidoReport(pedidoId); // (message);

            // Set headers for PDF response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // headers.setContentDispositionFormData("attachment", "report.pdf");
            headers.setContentDispositionFormData("inline", "pedidoReport.pdf");
            // headers.setContentDispositionFormData("inline", "report.pdf");

            return new ResponseEntity<>(report, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("Error LIA:" + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/oden_compra")
    public ResponseEntity<byte[]> generateOrdenCompraReport(@RequestParam Integer ordenId) {

        System.out.println("pedido=" + ordenId);

        try {
            // Generate the report as a byte array
            byte[] report = reportService.generateOrdenCompraReport(ordenId); // (message);

            // Set headers for PDF response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // headers.setContentDispositionFormData("attachment", "report.pdf");
            headers.setContentDispositionFormData("inline", "ordenCompraReport.pdf");
            // headers.setContentDispositionFormData("inline", "report.pdf");

            return new ResponseEntity<>(report, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("Error LIA:" + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
