package com.coagronet.espacioOcupacion.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EspacioOcupacionDTO {
    private Integer id;
    private Integer espacio;
    private Integer actividadOcupacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer estado;
}
