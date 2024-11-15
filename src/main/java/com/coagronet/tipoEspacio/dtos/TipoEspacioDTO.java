package com.coagronet.tipoEspacio.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoEspacioDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer estado;
}
