package com.coagronet.grupo.dtos;

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
public class GrupoDTO {

	private Long id;

	@NotBlank(message = "El nombre es obligatorio.")
	@Size(max = 255, message = "El nombre no debe superar los 255 caracteres.")
	private String nombre;

	private Long empresaId;

	@Size(max = 2048, message = "La descripción no debe superar los 2048 caracteres.")
	private String descripcion;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

}
