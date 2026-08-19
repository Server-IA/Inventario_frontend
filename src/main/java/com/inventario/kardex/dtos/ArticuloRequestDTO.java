package com.inventario.kardex.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ArticuloRequestDTO(
		@NotNull(message = "El ID de la presentación del producto es obligatorio") Long presentacionProductoId,

		@NotNull(message = "La cantidad es obligatoria") @Min(value = 1,
				message = "La cantidad debe ser mayor a cero") Integer cantidad,

		@NotNull(message = "El precio es obligatorio") @Min(value = 0,
				message = "El precio no puede ser negativo") BigDecimal precio,

		@NotNull(message = "Debe especificar si el producto es devolutivo") boolean devolutivo,

		Long responsableId,

		String lote,

		LocalDate fechaVencimiento) {
}
