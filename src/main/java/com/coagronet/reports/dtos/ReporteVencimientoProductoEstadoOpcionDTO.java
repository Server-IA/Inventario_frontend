package com.coagronet.reports.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Opcion disponible para el filtro Estado")
public record ReporteVencimientoProductoEstadoOpcionDTO(
		@Schema(example = "VENCIDO") String codigo,
		@Schema(example = "Vencido") String nombre) {
}
