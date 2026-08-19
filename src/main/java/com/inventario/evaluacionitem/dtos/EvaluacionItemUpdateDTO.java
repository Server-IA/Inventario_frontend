package com.inventario.evaluacionitem.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluacionItemUpdateDTO {

    private Long evaluacionId;
    private Long criterioEvaluacionId;
    private Integer valor;
    private String descripcion;
    private Long estadoId;
}

