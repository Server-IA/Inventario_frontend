package com.coagronet.reports.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Valores preseleccionables por ser la unica opcion disponible en su nivel")
public record ReporteKardexSeleccionInicialDTO(
		Long paisId,
		Long departamentoId,
		Long municipioId,
		Long sedeId) {
}
