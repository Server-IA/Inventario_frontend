package com.coagronet.municipio.dtos;

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
public class MunicipioDTO {

	private Long id;

	@NotBlank(message = "El nombre es obligatorio.")
	@Size(max = 60, message = "El nombre no debe superar los 60 caracteres.")
	private String nombre;

	@NotNull(message = "El departamento es obligatorio.")
	private Long departamentoId;

	private Integer codigo;

	private String acronimo;

	private Long empresaId;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

}
