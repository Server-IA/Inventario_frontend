package com.coagronet.kardex.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloRequestDTO {

	@NotNull(message = "El ID de la presentación del producto es obligatorio")
	private Long presentacionProductoId;

	@NotNull(message = "La cantidad es obligatoria")
	@Min(value = 1, message = "La cantidad debe ser mayor a cero")
	private Integer cantidad;

	@NotNull(message = "El precio es obligatorio")
	@Min(value = 0, message = "El precio no puede ser negativo")
	private BigDecimal precio;

	@NotNull(message = "Debe especificar si el producto es devolutivo")
	private boolean devolutivo;

	private Long responsableId;

}
