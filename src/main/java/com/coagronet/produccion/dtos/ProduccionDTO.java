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

	@NotNull(message = "El nombre no puede ser nulo")
	@Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
	private String nombre;

	@NotNull(message = "El tipo de producción no puede ser nulo")
	private Long tipoProduccionId;

	@Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
	private String descripcion;

	private LocalDateTime fechaInicio;

	private LocalDateTime fechaFinal;

	@NotNull(message = "El espacio no puede ser nulo")
	private Long espacioId;

	@NotNull(message = "El estado no puede ser nulo")
	private Long estadoId;

	@NotNull(message = "El producto no puede ser nulo")
	private Long productoId;

	private Long empresaId;

}
