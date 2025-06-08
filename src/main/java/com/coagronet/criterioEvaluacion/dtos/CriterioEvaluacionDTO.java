package com.coagronet.criterioEvaluacion.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CriterioEvaluacionDTO {
    private Long id;
    private Long tipoEvaluacion;
    private String nombre;
    private String descripcion;
    private Long estado;
}
