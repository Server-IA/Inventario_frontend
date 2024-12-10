package com.coagronet.producto.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private Long productoCategoria;
    private String descripcion;
    private Integer estado;
    private Long empresa;
}
