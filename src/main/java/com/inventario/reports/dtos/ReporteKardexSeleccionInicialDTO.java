/*=============================================================================
 Nombre del archivo : ReporteKardexSeleccionInicialDTO.java
 Descripcion        : DTO con la seleccion inicial de ubicacion del reporte Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-06-19 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Valores preseleccionables por ser la unica opcion disponible en su nivel")
public record ReporteKardexSeleccionInicialDTO(
		Long paisId,
		Long departamentoId,
		Long municipioId,
		Long sedeId,
		Long bloqueId,
		Long espacioId,
		Long almacenId) {
}
