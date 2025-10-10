package com.coagronet.pedidocotizacion.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PedidoCotizacionRequestDTO(
		@Size(max = 2048, message = "{validation.descripcion.length}") String descripcion,

		@Size(max = 2048, message = "{validation.archivo.length}") String archivo,

		@NotNull(message = "{validation.pedido.not-null}") Long pedidoId,

		@NotNull(message = "{validation.proveedor.not-null}") Long proveedorId,

		Long estadoId

) {

}
