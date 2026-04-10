package com.coagronet.kardex.dtos;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record KardexUpdateRequestDTO(@NotNull(message = "El ID del almac?n es obligatorio") Long almacenId,
		Long almacenDestinoId, Long ordenCompraId, Long pedidoId, Long produccionId, Long clienteProveedorId,
		String descripcion, @NotEmpty(
				message = "El movimiento de Kardex debe contener al menos un art?culo") @Valid List<ArticuloUpdateRequestDTO> items) {
}
