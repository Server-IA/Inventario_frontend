package com.coagronet.producto.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoDTO {

	private Long id;

	@Size(max = 255)
	@NotNull(message = "El nombre no puede ser nulo")
	private String nombre;

	@NotNull(message = "Producto categoría no puede ser nulo")
	private Long productoCategoriaId;

	@NotNull(message = "Producto no puede tener una unidad minima nula")
	private Long unidadMinimaId;

	@NotNull(message = "el campo ingredientePresentacionProductoId no puede ser nulo")
	private Long ingredientePresentacionProductoId;

	@Size(max = 255)
	private String descripcion;

	@NotNull
	private Long estadoId;

	private Long empresaId;

}
