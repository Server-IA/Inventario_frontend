package com.coagronet.reports.controllers;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.coagronet.reports.services.ReportService;

@RestController
@RequestMapping("/api/v2/report")
public class ReportController {

	private final String version = "2.0.1";

	private final ReportService reportService;

	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping("/version")
	public ResponseEntity<String> version(){
		return ResponseEntity.ok("Version del controlador de reportes " +version);
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
