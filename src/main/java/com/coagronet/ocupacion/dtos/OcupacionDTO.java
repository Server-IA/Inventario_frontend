package com.coagronet.ocupacion.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OcupacionDTO {

	private Long id;

	@NotBlank(message = "El nombre es obligatorio.")
	@Size(max = 100, message = "El nombre no debe superar los 100 caracteres.")
	private String nombre;

	@NotNull(message = "El tipo de actividad es obligatorio.")
	private Long tipoActividadId;

	@NotNull(message = "La evaluación es obligatoria.")
	private Long evaluacionId;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

	private Long empresaId;

}
