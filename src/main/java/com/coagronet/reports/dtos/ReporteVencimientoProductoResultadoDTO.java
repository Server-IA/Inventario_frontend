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
