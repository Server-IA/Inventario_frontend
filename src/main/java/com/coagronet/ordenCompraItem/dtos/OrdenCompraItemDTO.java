package com.coagronet.ordenCompraItem.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrdenCompraItemDTO {
    private Integer id;
    private Long ordenCompra;
    private Integer productoPresentacion;
    private Double cantidad;
    private Double precio;
    private Integer estado;
}
