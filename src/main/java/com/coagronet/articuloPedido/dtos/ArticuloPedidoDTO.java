package com.coagronet.articuloPedido.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticuloPedidoDTO {

	private Long id;

	private Double cantidad;

	@NotNull(message = "El kardex es obligatorio.")
	private Long pedidoId;

	@NotNull(message = "La presentación de producto es obligatoria.")
	private Long presentacionProductoId;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

	private Long empresaId;

}