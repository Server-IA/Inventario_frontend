package com.coagronet.kardex.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle de un artículo dentro del movimiento de Kardex para su edición. Los campos nulos son omitidos en la respuesta.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArticuloUpdateResponseDTO(

		@Schema(description = "ID único del detalle del artículo en el Kardex", example = "5012") Long id,

		@Schema(description = "ID de la presentación del producto", example = "150") Long presentacionProductoId,

		@Schema(description = "Cantidad del producto involucrada en el movimiento", example = "10.50") BigDecimal cantidad,

		@Schema(description = "Precio unitario del producto en el momento del movimiento", example = "25000.00") BigDecimal precio,

		@Schema(description = "ID del usuario o empleado responsable de este artículo (ej. para productos devolutivos)", example = "42", nullable = true) Long responsableId,

		@Schema(description = "Número de lote del producto", example = "LOTE-2026-A", nullable = true) String lote,

		@Schema(description = "Fecha de vencimiento del lote", example = "2026-12-31", nullable = true) LocalDate fechaVencimiento) {
}