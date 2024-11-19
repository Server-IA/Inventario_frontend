package com.coagronet.tipoProduccion.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoProduccionDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer estado;
}
