package com.inventario.evaluacionitem.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluacionItemCreateDTO {

    @NotNull(message = "La evaluación no puede ser nula")
    private Long evaluacionId;

    private Integer valor;
    @NotNull(message = "El criterio de evaluacion no puede ser nulo")
    private Long criterioEvaluacionId;

    private String descripcion;


}
