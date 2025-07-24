package com.coagronet.kardex.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KardexDTO {

	private Long id;

	private LocalDateTime fechaHora;

	@NotNull(message = "El id del almacén no puede ser nulo")
	private Long almacenId;

	@NotNull(message = "El id de producción no puede ser nulo")
	private Long produccionId;

	@NotNull(message = "El id del tipo de movimiento no puede ser nulo")
	private Long tipoMovimientoId;

	@Size(max = 500, message = "La descripción debe tener máximo 500 caracteres")
	private String descripcion;

	@NotNull(message = "El id del estado no puede ser nulo")
	private Long estadoId;

	private Long empresaId;

}
