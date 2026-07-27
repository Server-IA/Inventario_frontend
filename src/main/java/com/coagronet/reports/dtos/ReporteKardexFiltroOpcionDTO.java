/*=============================================================================
 Nombre del archivo : ReporteKardexFiltroOpcionDTO.java
 Descripcion        : DTO de opcion para los filtros del reporte Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-06-19 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Opcion disponible para un filtro del reporte Kardex")
public record ReporteKardexFiltroOpcionDTO(
		@Schema(example = "1") Long id,
		@Schema(example = "Colombia") String nombre,
		@Schema(description = "Identificador del nivel superior en la cascada", example = "10") Long padreId) {
}
