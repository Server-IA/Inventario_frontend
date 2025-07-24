package com.coagronet.unidad.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UnidadDTO {

	private Long id;

	@NotBlank
	private String nombre;

	@Size(max = 255, message = "La descripcion no debe ser mayor a 255 caracteres")
	private String descripcion;

	private Long estadoId;

	private Long empresaId;

}
