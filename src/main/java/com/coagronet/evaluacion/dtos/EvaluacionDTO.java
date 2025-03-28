package com.coagronet.evaluacion.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EvaluacionDTO {
    private Integer id;
    private Integer tipoEvaluacionId;
    private LocalDateTime fechaHora;
    private Long empresaId;
    private Integer evaluado;
    private Integer estadoId;
}
