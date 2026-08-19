/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoFiltroDTO.java
 Descripcion        : DTO de filtros para consultar vencimientos de producto.
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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Filtros de consulta para el reporte de vencimiento de producto")
public record ReporteVencimientoProductoFiltroDTO(
		@Schema(description = "Filtro opcional por pais", example = "1") Long paisId,
		@Schema(description = "Filtro opcional por departamento", example = "10") Long departamentoId,
		@Schema(description = "Filtro opcional por municipio", example = "20") Long municipioId,
		@Schema(description = "Filtro minimo recomendado por sede", example = "30") Long sedeId,
		@Schema(description = "Filtro opcional por bloque", example = "40") Long bloqueId,
		@Schema(description = "Filtro opcional por espacio", example = "50") Long espacioId,
		@Schema(description = "Filtro minimo permitido por almacen", example = "60") Long almacenId,
		@Schema(description = "Filtro opcional por categoria de producto", example = "5") Long categoriaId,
		@Schema(description = "Filtro opcional por producto", example = "12") Long productoId,
		@Schema(description = "Filtro opcional por presentacion de producto", example = "21") Long presentacionId,
		@Schema(description = "Fecha inicial del rango de vencimiento", example = "2026-07-01") @NotNull(message = "{report.vencimiento.fecha-inicio.required}") LocalDate fechaInicio,
		@Schema(description = "Fecha final del rango de vencimiento", example = "2026-07-31") @NotNull(message = "{report.vencimiento.fecha-fin.required}") LocalDate fechaFin,
		@Schema(description = "Estado calculado a consultar: VENCIDO, PROXIMO_A_VENCER o TODOS", example = "TODOS") ReporteVencimientoProductoEstado estado) {

	public ReporteVencimientoProductoEstado estadoNormalizado() {
		return ReporteVencimientoProductoEstado.normalize(estado);
	}

	public boolean tieneUbicacionMinima() {
		return paisId != null
				|| departamentoId != null
				|| municipioId != null
				|| sedeId != null
				|| bloqueId != null
				|| espacioId != null
				|| almacenId != null;
	}

}
