package com.inventario.kardex.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con los datos de cabecera y detalle de un movimiento de Kardex para su edición. Los campos nulos son omitidos en la respuesta.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record KardexUpdateResponseDTO(

		@Schema(description = "ID único del movimiento de Kardex", example = "1024") Long id,

		@Schema(description = "ID del tipo de movimiento (Entrada, Salida, Traslado, etc.)", example = "1") Long tipoMovimientoId,

		@Schema(description = "ID del almacén principal o de origen", example = "5") Long almacenId,

		@Schema(description = "ID del almacén de destino (solo aplica para traslados)", example = "8", nullable = true) Long almacenDestinoId,

		@Schema(description = "ID de la orden de compra asociada", example = "2048", nullable = true) Long ordenCompraId,

		@Schema(description = "ID del pedido asociado", example = "4096", nullable = true) Long pedidoId,

		@Schema(description = "ID de la orden de producción asociada", example = "8192", nullable = true) Long produccionId,

		@Schema(description = "ID del cliente o proveedor asociado al movimiento", example = "300", nullable = true) Long clienteProveedorId,

		@Schema(description = "Descripción o notas adicionales del movimiento", example = "Ajuste de inventario por revisión mensual", nullable = true) String descripcion,

		@Schema(description = "Lista de artículos asociados a este movimiento de Kardex") List<ArticuloUpdateResponseDTO> items) {
}
