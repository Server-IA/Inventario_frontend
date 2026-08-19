package com.inventario.criterioEvaluacion.dtos;

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
public class CriterioEvaluacionDTO {

	private Long id;

	@Size(max = 100, message = "El nombre debe tener máximo 100 caracteres.")
	@NotBlank(message = "El nombre no puede estar vacío.")
	private String nombre;

	@Size(max = 2048, message = "La descripción debe tener máximo 2048 caracteres.")
	private String descripcion;

	@NotNull(message = "El ID del tipo de evaluación no puede ser nulo.")
	private Long tipoEvaluacionId;

	@NotNull(message = "El ID del estado no puede ser nulo.")
	private Long estadoId;

	private Long empresaId;

}
