package com.coagronet.tipoBloque.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoBloqueDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long estadoId;
    private Long empresaId;
}
