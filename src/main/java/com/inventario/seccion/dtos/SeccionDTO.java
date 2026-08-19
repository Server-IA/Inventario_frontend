package com.inventario.seccion.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeccionDTO {

	private Long id;

	@NotBlank(message = "El nombre de la sección no puede estar vacío")
	@Size(max = 100, message = "El nombre de la sección no puede tener más de 100 caracteres")
	private String nombre;

	@Size(max = 2048, message = "La descripción no puede tener más de 2048 caracteres")
	private String descripcion;

	@NotNull(message = "El espacio es obligatorio")
	private Long espacioId;

	@NotNull(message = "El estado es obligatorio")
	private Long estadoId;

	private Long empresaId;

}