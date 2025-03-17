package com.coagronet.criterioEvaluacion.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CriterioEvaluacionDTO {
    private Integer id;
    private Integer tipoEvaluacion;
    private String nombre;
    private String descripcion;
    private Integer estado;
}
