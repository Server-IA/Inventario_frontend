package com.coagronet.unidad.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UnidadDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long estado;
    private Long empresa;
}
