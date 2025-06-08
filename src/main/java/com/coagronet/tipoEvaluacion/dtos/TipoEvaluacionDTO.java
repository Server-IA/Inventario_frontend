package com.coagronet.tipoEvaluacion.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TipoEvaluacionDTO {
    private Long id;
    private String nombre;
    private Long estadoId;
}
