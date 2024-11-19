package com.coagronet.productoPresentacion.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoPresentacionDTO {
    private Integer id;
    private Integer producto;
    private String nombre;
    private Integer unidad;
    private String descripcion;
    private Integer estado;
    private Double cantidad;
    private Integer marca;
    private Integer presentacion;
}
