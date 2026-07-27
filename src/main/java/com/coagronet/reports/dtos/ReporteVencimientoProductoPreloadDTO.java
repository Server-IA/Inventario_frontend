/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoPreloadDTO.java
 Descripcion        : DTO de respuesta para precargar filtros de vencimientos de producto.
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
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de precarga para inicializar el modulo Vencimiento Producto")
public record ReporteVencimientoProductoPreloadDTO(
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
		List<ReporteVencimientoProductoRangoRapidoDTO> rangosRapidos,
		List<ReporteVencimientoProductoEstadoOpcionDTO> estados,
		ReporteVencimientoProductoSeleccionInicialDTO seleccionInicial) {
}
