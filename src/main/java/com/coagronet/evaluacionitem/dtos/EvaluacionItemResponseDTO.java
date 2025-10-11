package com.coagronet.evaluacionitem.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionItemResponseDTO {

    private Long id;
    private Integer valor;
    private Long evaluacionId;
    private Long criterioEvaluacionId;
    private String descripcion;
    private Long estadoId;
    private Long empresaId;
}
