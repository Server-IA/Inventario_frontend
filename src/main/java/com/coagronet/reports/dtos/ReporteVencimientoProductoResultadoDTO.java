/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoResultadoDTO.java
 Descripcion        : DTO de resultado para productos vencidos o proximos a vencer.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-10 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Fila de resultado para la previsualizacion del reporte Vencimiento Producto")
public record ReporteVencimientoProductoResultadoDTO(
		Long kardexItemId,
		@Schema(example = "Fertilizante foliar") String producto,
		@Schema(example = "PROXIMO_A_VENCER") String estadoCodigo,
		@Schema(example = "Proximo a vencer") String estado,
		LocalDate fechaVencimiento,
		BigDecimal cantidad,
		Long paisId,
		String pais,
		Long departamentoId,
		String departamento,
		Long municipioId,
		String municipio,
		Long sedeId,
		String sede,
		Long bloqueId,
		String bloque,
		Long espacioId,
		String espacio,
		Long almacenId,
		String almacen,
		String ubicacionCompleta) {
}
