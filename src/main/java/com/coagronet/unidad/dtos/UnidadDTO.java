package com.coagronet.unidad.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UnidadDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer estado;
    private Long empresa;
}
