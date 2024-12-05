package com.coagronet.tipoEspacio.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TipoEspacioDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer estado;
    private Long empresa;
}
