/*=============================================================================
 Nombre del archivo : ReportePedidoConsultaResponseDTO.java
 Descripcion        : DTO de respuesta para la consulta del resumen de pedidos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-18 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.dtos;

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
