package com.coagronet.productoCategoria.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoCategoriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer estado;
    private Long empresa;
}
