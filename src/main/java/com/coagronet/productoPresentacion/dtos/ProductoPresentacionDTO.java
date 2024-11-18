package com.coagronet.productoPresentacion.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
