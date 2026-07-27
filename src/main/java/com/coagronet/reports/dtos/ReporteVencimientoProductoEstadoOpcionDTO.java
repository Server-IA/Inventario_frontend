/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoEstadoOpcionDTO.java
 Descripcion        : DTO de opcion de estado para el reporte de vencimiento de producto.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-10 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Opcion disponible para el filtro Estado")
public record ReporteVencimientoProductoEstadoOpcionDTO(
		@Schema(example = "VENCIDO") String codigo,
		@Schema(example = "Vencido") String nombre) {
}
