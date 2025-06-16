package com.coagronet.evaluacion.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EvaluacionDTO {
    private Long id;
    private Long tipoEvaluacionId;
    private LocalDateTime fechaHora;
    private Long empresaId;
    private Long evaluado;
    private Long estadoId;
}
