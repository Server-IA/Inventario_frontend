package com.inventario.evaluacion.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvaluacionDTO {

	private Long id;

	private LocalDateTime fechaHora;

	private Integer idEntidadEvaluada;

	@NotNull(message = "El campo 'tipoEvaluacionId' no puede ser nulo")
	private Long tipoEvaluacionId;

	@NotNull(message = "El campo 'estadoId' no puede ser nulo")
	private Long estadoId;

	private Long empresaId;

}
