package com.coagronet.reports.dtos;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta del resumen previo del reporte de pedido")
public record ReportePedidoConsultaResponseDTO(
		LocalDateTime fechaGeneracion,
		int totalPedidos,
		String mensaje,
		List<ReportePedidoResumenItemDTO> pedidos) {
}
