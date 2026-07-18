package com.coagronet.reports.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de totales de un pedido antes de exportarlo")
public record ReportePedidoResumenItemDTO(
		Long pedidoId,
		LocalDateTime fechaPedido,
		String estado,
		String almacen,
		int cantidadProductos,
		BigDecimal totalCantidad,
		boolean pedidoSinProductos,
		boolean unidadFaltante,
		String advertencia) {
}
