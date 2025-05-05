package com.coagronet.estado.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstadoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String acronimo;
}
