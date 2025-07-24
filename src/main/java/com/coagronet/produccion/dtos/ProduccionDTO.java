package com.coagronet.produccion.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class ProduccionDTO {

	private Long id;

	@NotNull(message = "nombre must not be null")
	@Size(max = 100, message = "nombre must not exceed 100 characters")
	private String nombre;

	@Size(max = 2048, message = "descripcion must not exceed 2048 characters")
	private String descripcion;

	private LocalDateTime fechaInicio;

	private LocalDateTime fechaFinal;

	@NotNull(message = "tipoProduccionId must not be null")
	private Long tipoProduccionId;

	@NotNull(message = "espacioId must not be null")
	private Long espacioId;

	@NotNull(message = "subSeccionId must not be null")
	private Long subSeccionId;

	@NotNull(message = "estadoId must not be null")
	private Long estadoId;

	private Long empresaId;

}
