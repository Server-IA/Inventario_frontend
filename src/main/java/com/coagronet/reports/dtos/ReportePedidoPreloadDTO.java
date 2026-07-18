package com.coagronet.reports.dtos;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para inicializar los filtros del reporte de pedido")
public record ReportePedidoPreloadDTO(
		LocalDate fechaInicio,
		LocalDate fechaFin,
		boolean ubicacionDisponible,
		List<ReporteKardexFiltroOpcionDTO> pedidos,
		List<ReporteKardexFiltroOpcionDTO> estados,
		List<ReporteKardexFiltroOpcionDTO> paises,
		List<ReporteKardexFiltroOpcionDTO> departamentos,
		List<ReporteKardexFiltroOpcionDTO> municipios,
		List<ReporteKardexFiltroOpcionDTO> sedes,
		List<ReporteKardexFiltroOpcionDTO> bloques,
		List<ReporteKardexFiltroOpcionDTO> espacios,
		List<ReporteKardexFiltroOpcionDTO> almacenes,
		ReporteKardexSeleccionInicialDTO seleccionInicial) {
}
