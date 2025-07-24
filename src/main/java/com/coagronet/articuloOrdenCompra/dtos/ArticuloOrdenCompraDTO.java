package com.coagronet.articuloOrdenCompra.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticuloOrdenCompraDTO {

	private Long id;

	private Double cantidad;

	private Double precio;

	@NotNull(message = "La orden de compra es obligatoria.")
	private Long ordenCompraId;

	@NotNull(message = "La presentación de producto es obligatoria.")
	private Long presentacionProductoId;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

	private Long empresaId;

}
