package com.coagronet.tipoEspacio.dtos;

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
public class TipoEspacioDTO {

	private Long id;

	@NotBlank(message = "El nombre es obligatorio.")
	@Size(max = 60, message = "El nombre no debe superar los 60 caracteres.")
	private String nombre;

	private String descripcion;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

	private Long empresaId;
}
