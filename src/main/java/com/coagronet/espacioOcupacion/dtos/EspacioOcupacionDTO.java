package com.coagronet.espacioOcupacion.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EspacioOcupacionDTO {
    private Long id;
    private Long espacio;
    private Integer actividadOcupacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long estado;
}
