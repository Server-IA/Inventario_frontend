package com.coagronet.kardex.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Detalle del artículo a actualizar o agregar en el Kardex")
public record ArticuloUpdateRequestDTO(

		@Schema(description = "ID del detalle existente. Si es nulo, se creará un nuevo registro", example = "10", nullable = true) Long id,

		@Schema(description = "ID de la presentación del producto", example = "5") @NotNull(message = "El ID de la presentación es obligatorio") Long presentacionProductoId,

		@Schema(description = "Cantidad del producto a mover", example = "10.00") @NotNull(message = "La cantidad es obligatoria") @Min(value = 1) BigDecimal cantidad,

		@Schema(description = "Precio unitario del producto en este movimiento", example = "15000.00") @NotNull(message = "El precio es obligatorio") @Min(value = 0) BigDecimal precio,

		@Schema(description = "Indica si el producto exige devolución. Si es true, el sistema lo desagregará por unidad y requerirá un responsable.", example = "false") @NotNull(message = "Debe especificar si el producto es devolutivo") boolean devolutivo,

		@Schema(description = "ID del usuario responsable. Obligatorio si el producto es devolutivo.", example = "102", nullable = true) Long responsableId,

		@Schema(description = "Número de lote del producto", example = "LOTE-2023-XYZ", nullable = true) String lote,

		@Schema(description = "Fecha de vencimiento del producto o lote", example = "2025-12-31", nullable = true) LocalDate fechaVencimiento) {
}