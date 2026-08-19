package com.inventario.kardex.dtos;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record KardexRequestDTO(@NotNull(message = "El ID del tipo de movimiento es obligatorio") Long tipoMovimientoId,

		@NotNull(message = "El ID del almacén es obligatorio") Long almacenId,

		Long almacenDestinoId, Long ordenCompraId, Long pedidoId, Long produccionId,

		Long clienteProveedorId,

		String descripcion,

		@NotEmpty(
				message = "El movimiento de Kardex debe contener al menos un artículo") @Valid List<ArticuloRequestDTO> items) {
}
