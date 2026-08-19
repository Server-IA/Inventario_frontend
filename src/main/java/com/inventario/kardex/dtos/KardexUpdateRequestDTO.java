package com.inventario.kardex.dtos;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Objeto que representa la solicitud de actualización de un movimiento de Kardex")
public record KardexUpdateRequestDTO(

		@Schema(description = "ID del almacén de origen", example = "1") @NotNull(message = "El ID del almacén es obligatorio") Long almacenId,

		@Schema(description = "ID del almacén de destino (para traslados)", example = "2", nullable = true) Long almacenDestinoId,

		@Schema(description = "ID de la orden de compra asociada", example = "1050", nullable = true) Long ordenCompraId,

		@Schema(description = "ID del pedido asociado", example = "302", nullable = true) Long pedidoId,

		@Schema(description = "ID del proceso de producción asociado", example = "45", nullable = true) Long produccionId,

		@Schema(description = "ID del cliente o proveedor asociado", example = "89", nullable = true) Long clienteProveedorId,

		@Schema(description = "Observaciones o descripción del movimiento", example = "Ajuste de inventario por revisión mensual", nullable = true) String descripcion,

		@Schema(description = "Lista de artículos involucrados en el movimiento") @NotEmpty(message = "El movimiento de Kardex debe contener al menos un artículo") @Valid List<ArticuloUpdateRequestDTO> items) {
}
