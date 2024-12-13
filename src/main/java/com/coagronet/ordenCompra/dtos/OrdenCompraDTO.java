package com.coagronet.ordenCompra.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrdenCompraDTO {
    private Long id;
    private LocalDateTime fechaHora;
    private Integer pedido;
    private Integer proveedor;
    private String descripcion;
    private Integer estado;
}
