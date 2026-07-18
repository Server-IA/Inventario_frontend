package com.coagronet.reports.dtos;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Filtros de consulta y exportacion del reporte de pedido")
public record ReportePedidoFiltroDTO(
		@Schema(description = "Identificadores de los pedidos seleccionados")
		@Size(max = 500, message = "{report.pedido.pedidos.max}") List<Long> pedidoIds,
		@Schema(description = "Estado del pedido", example = "1") Long estadoId,
		@Schema(description = "Fecha inicial inclusiva", example = "2026-07-01") LocalDate fechaInicio,
		@Schema(description = "Fecha final inclusiva", example = "2026-07-31") LocalDate fechaFin,
		@Schema(description = "Pais", example = "1") Long paisId,
		@Schema(description = "Departamento", example = "10") Long departamentoId,
		@Schema(description = "Municipio", example = "20") Long municipioId,
		@Schema(description = "Sede", example = "30") Long sedeId,
		@Schema(description = "Bloque", example = "40") Long bloqueId,
		@Schema(description = "Espacio", example = "50") Long espacioId,
		@Schema(description = "Almacen", example = "60") Long almacenId) {
}
