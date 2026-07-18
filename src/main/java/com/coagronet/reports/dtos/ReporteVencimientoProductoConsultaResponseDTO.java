package com.coagronet.reports.dtos;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de busqueda del reporte Vencimiento Producto")
public record ReporteVencimientoProductoConsultaResponseDTO(
		LocalDate fechaGeneracion,
		int total,
		String mensaje,
		List<ReporteVencimientoProductoResultadoDTO> resultados) {
}
