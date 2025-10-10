package com.coagronet.pedidocotizacion.dtos;

public record PedidoCotizacionResponseDTO(Long id,

		String descripcion,

		String archivo,

		Long pedidoId,

		Long proveedorId,

		Long estadoId) {
}
