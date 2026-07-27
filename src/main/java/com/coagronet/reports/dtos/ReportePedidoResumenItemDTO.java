/*=============================================================================
 Nombre del archivo : ReportePedidoResumenItemDTO.java
 Descripcion        : DTO con el resumen de cantidades y totales de un pedido.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-18 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
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
