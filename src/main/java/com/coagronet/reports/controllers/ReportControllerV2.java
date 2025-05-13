package com.coagronet.reports.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.coagronet.reports.services.ReportService;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/report")
@CrossOrigin(origins = "*")
public class ReportControllerV2 {

    private final ReportService reportService;



    public ReportControllerV2(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/{reportName}")
    public ResponseEntity<byte[]> generarReporteSQL(@PathVariable String reportName,
                                                    @RequestBody Map<String, Object> parametros) {

        byte[] reporte = reportService.generarReporte(reportName, parametros);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("reporte", reportName + ".pdf");

        return new ResponseEntity<>(reporte, headers, HttpStatus.OK);
    }



}
