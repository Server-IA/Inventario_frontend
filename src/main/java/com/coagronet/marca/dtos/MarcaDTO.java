package com.coagronet.marca.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarcaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer estado;
}
