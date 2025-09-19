package com.coagronet.ordenCompra.dtos;

import java.time.LocalDateTime;

import com.coagronet.validation.EstadoOrdenCompra;
import com.coagronet.validation.PedidoExists;
import com.coagronet.validation.ProveedorExists;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdenCompraDTO {

	private Long id;

	@Size(max = 2048, message = "{validation.descripcion.length}")
	private String descripcion;

	private LocalDateTime fechaHora;

	@NotNull(message = "{validation.pedido.not-null}")
	@PedidoExists(message = "{pedido.not-found}")
	private Long pedidoId;

	@NotNull(message = "{validation.proveedor.not-null}")
	@ProveedorExists(message = "{proveedor.not-found}")
	private Long proveedorId;

	@NotNull(message = "{validation.estado.not-null}")
	@EstadoOrdenCompra(value = 3, message = "{validation.orden-compra.estado.invalid-category}")
	private Long estadoId;

	private Long empresaId;

}
