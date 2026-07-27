/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoSeleccionInicialDTO.java
 Descripcion        : DTO con la seleccion inicial del reporte de vencimiento de producto.
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

@Schema(description = "Valores preseleccionables por ser la unica opcion disponible en su nivel")
public record ReporteVencimientoProductoSeleccionInicialDTO(
		Long paisId,
		Long departamentoId,
		Long municipioId,
		Long sedeId,
		Long bloqueId,
		Long espacioId,
		Long almacenId) {
}
