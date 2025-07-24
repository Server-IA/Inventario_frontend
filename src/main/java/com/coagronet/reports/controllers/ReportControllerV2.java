package com.coagronet.reports.controllers;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.reports.services.ReportService;

@RestController
@RequestMapping("/api/v2/report")
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
