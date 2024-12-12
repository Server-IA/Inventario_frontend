package com.coagronet.pedidoItem.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PedidoItemDTO {
    private Long id;
    private Integer pedido;
    private Integer productoPresentacion;
    private Double cantidad;
    private Integer estado;
}
