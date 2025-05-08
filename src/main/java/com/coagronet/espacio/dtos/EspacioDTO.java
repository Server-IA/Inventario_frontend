package com.coagronet.espacio.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EspacioDTO {
    private Long id;
    private Long bloque;
    private Integer tipoEspacio;
    private String nombre;
    private String geolocalizacion;
    private String coordenadas;
    private String descripcion;
    private Long estado;
}
