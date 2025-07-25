package com.coagronet.ingredientePresentacionProducto.dtos;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngredientePresentacionProductoDTO {

	private Long id;

	@NotBlank(message = "El campo nombre no puede estar vacío.")
	@Length(max = 100, message = "El campo nombre no debe superar los 100 caracteres.")
	private String nombre;

	@Length(max = 2048, message = "El campo descripcion no debe superar los 2048 caracteres.")
	private String descripcion;

	@NotNull(message = "El campo ingredienteId no puede ser nulo.")
	private Long ingredienteId;

	@NotNull(message = "El campo presentacionProductoId no puede ser nulo.")
	private Long presentacionProductoId;

	@NotNull(message = "El campo estadoId no puede ser nulo.")
	private Long estadoId;

	private Long empresaId;

}
