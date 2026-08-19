/*=============================================================================
 Nombre del archivo : ReporteKardexPreloadDTO.java
 Descripcion        : DTO de respuesta para la precarga de filtros del reporte Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-06-19 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.dtos;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de precarga para inicializar el modulo de Reporte Kardex")
public record ReporteKardexPreloadDTO(
		LocalDate fechaInicio,
		LocalDate fechaFin,
		boolean ubicacionDisponible,
		List<ReporteKardexFiltroOpcionDTO> paises,
		List<ReporteKardexFiltroOpcionDTO> departamentos,
		List<ReporteKardexFiltroOpcionDTO> municipios,
		List<ReporteKardexFiltroOpcionDTO> sedes,
		List<ReporteKardexFiltroOpcionDTO> bloques,
		List<ReporteKardexFiltroOpcionDTO> espacios,
		List<ReporteKardexFiltroOpcionDTO> almacenes,
		List<ReporteKardexFiltroOpcionDTO> categorias,
		List<ReporteKardexFiltroOpcionDTO> productos,
		List<ReporteKardexFiltroOpcionDTO> presentaciones,
		List<ReporteKardexFiltroOpcionDTO> producciones,
		ReporteKardexSeleccionInicialDTO seleccionInicial) {
}
