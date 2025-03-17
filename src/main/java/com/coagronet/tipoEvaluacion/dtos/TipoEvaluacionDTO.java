package com.coagronet.tipoEvaluacion.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TipoEvaluacionDTO {
    private Integer id;
    private String nombre;
    private Integer estado;
}
