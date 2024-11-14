package com.coagronet.tipoSede.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoSedeDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer estado;
}
