package com.coagronet.kardex.dtos;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KardexRequestDTO {

	@NotNull(message = "El ID del tipo de movimiento es obligatorio")
	private Long tipoMovimientoId;

	@NotNull(message = "El ID del almacén es obligatorio")
	private Long almacenId;

	private Long almacenDestinoId;

	private Long ordenCompraId;

	private Long pedidoId;

	private Long produccionId;

	@NotEmpty(message = "El movimiento de Kardex debe contener al menos un artículo")
	@Valid
	private List<ArticuloRequestDTO> items;

}
