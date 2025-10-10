package com.coagronet.evaluacionitem.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluacionItemCreateDTO {

    private Long evaluacionId;
    private Integer valor;
    private Long criterioEvaluacionId;
    private String descripcion;


}
