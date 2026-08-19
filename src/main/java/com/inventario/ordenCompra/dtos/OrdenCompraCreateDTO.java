package com.inventario.ordenCompra.dtos;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdenCompraCreateDTO {

	@Size(max = 2048, message = "{validation.descripcion.length}")
	private String descripcion;

	private LocalDateTime fechaHora;

	@NotNull(message = "{validation.pedido.not-null}")
	private Long pedidoId;

	@NotNull(message = "{validation.proveedor.not-null}")
	private Long proveedorId;

	private Long empresaId;

}
