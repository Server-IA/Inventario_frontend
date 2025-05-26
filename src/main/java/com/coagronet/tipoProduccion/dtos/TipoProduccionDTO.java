package com.coagronet.tipoProduccion.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoProduccionDTO {

	private Long id;

	@NotBlank(message = "El nombre es obligatorio.")
	@Size(max = 100, message = "El nombre no debe superar los 100 caracteres.")
	private String nombre;

	@Size(max = 255, message = "La descripción no debe superar los 255 caracteres.")
	private String descripcion;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

	private Long empresaId;

}