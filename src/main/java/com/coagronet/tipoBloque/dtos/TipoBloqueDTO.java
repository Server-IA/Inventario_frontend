package com.coagronet.tipoBloque.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TipoBloqueDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Long estado;
    private Long empresa;
}
