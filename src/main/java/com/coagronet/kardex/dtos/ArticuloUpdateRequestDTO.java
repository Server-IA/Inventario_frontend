package com.coagronet.kardex.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ArticuloUpdateRequestDTO(Long id,
		@NotNull(message = "El ID de la presentaci?n es obligatorio") Long presentacionProductoId,
		@NotNull(message = "La cantidad es obligatoria") @Min(value = 1) BigDecimal cantidad,
		@NotNull(message = "El precio es obligatorio") @Min(value = 0) BigDecimal precio,
		@NotNull(message = "Debe especificar si el producto es devolutivo") boolean devolutivo, Long responsableId,
		String lote, LocalDate fechaVencimiento) {
}