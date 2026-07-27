/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoRangoRapidoDTO.java
 Descripcion        : DTO de rangos rapidos para filtrar vencimientos de producto.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-10 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.dtos;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Rango de fechas predefinido para el date range picker")
public record ReporteVencimientoProductoRangoRapidoDTO(
		@Schema(example = "PROXIMOS_7_DIAS") String codigo,
		@Schema(example = "Proximos 7 dias") String nombre,
		LocalDate fechaInicio,
		LocalDate fechaFin) {
}
