package com.coagronet.reports.dtos;

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
