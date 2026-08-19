package com.inventario.pedidocotizacion.dtos;

/**
 * DTO de salida para representar una {@code PedidoCotizacion} al cliente.
 *
 * <p>
 * Incluye el identificador propio y los identificadores de las entidades
 * relacionadas,
 * además de los campos de texto principales.
 * </p>
 *
 * @param id          identificador de la cotización
 * @param descripcion descripción libre de la cotización
 * @param archivo     referencia a archivo asociado (ruta/URL/nombre)
 * @param pedidoId    identificador del pedido al que pertenece
 * @param proveedorId identificador del proveedor asociado
 * @param estadoId    identificador del estado general
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
public record PedidoCotizacionResponseDTO(Long id,

		String descripcion,

		String archivo,

		Long pedidoId,

		Long proveedorId,

		Long estadoId) {
}
