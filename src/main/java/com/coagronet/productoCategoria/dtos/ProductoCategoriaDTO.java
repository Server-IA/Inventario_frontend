package com.coagronet.productoCategoria.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoCategoriaDTO {

	private Long id;

	@NotBlank(message = "El nombre es obligatorio.")
	@Size(max = 255, message = "El nombre no debe superar los 255 caracteres.")
	private String nombre;

	@Size(max = 255, message = "La descripcion no debe superar los 255 caracteres.")
	private String descripcion;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

	private Long empresaId;

}