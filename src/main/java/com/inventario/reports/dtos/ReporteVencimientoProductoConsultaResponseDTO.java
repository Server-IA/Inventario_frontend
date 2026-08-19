/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoConsultaResponseDTO.java
 Descripcion        : DTO de respuesta para la consulta de vencimientos de producto.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-10 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.dtos;

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
