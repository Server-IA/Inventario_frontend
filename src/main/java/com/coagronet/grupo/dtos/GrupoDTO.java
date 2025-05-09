package com.coagronet.grupo.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrupoDTO {

    private Long id;
    private String nombre;
    private Long empresa;
    private String descripcion;
    private Long estado;
}
