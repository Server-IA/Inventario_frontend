package com.coagronet.tipoEvaluacion.dtos;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TipoEvaluacionDTO {

	private Long id;

	@NotBlank(message = "El campo nombre no puede ser nulo.")
	@Length(max = 100, message = "El campo nombre no puede exceder los 100 caracteres.")
	private String nombre;

	@Length(max = 2048, message = "El campo descripcion no puede exceder los 2048 caracteres.")
	private String descripcion;

	private Long estadoId;

}
