package com.coagronet.reports.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Valores preseleccionables por ser la unica opcion disponible en su nivel")
public record ReporteVencimientoProductoSeleccionInicialDTO(
		Long paisId,
		Long departamentoId,
		Long municipioId,
		Long sedeId,
		Long bloqueId,
		Long espacioId,
		Long almacenId) {
}
