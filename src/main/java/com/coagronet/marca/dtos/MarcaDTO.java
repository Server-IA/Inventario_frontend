package com.coagronet.marca.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MarcaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer estado;
    private Long empresa;
}
