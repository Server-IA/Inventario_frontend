package com.coagronet.tipoMovimiento.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TipoMovimientoDTO {

	private Long id;

	@NotBlank(message = "El nombre no puede ser vacío")
	@Size(max = 255, message = "El nombre no debe superar los 255 caracteres")
	private String nombre;

	@Size(max = 500, message = "La descripcion no debe superar los 500 caracteres")
	private String descripcion;

	@NotNull(message = "El id del estado no puede ser nulo")
	private Long estadoId;

	private Long empresaId;

	@NotNull(message = "El id del movimiento no puede ser nulo")
	private Long movimientoId;

}
