package com.coagronet.bloque.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BloqueDTO {
    private Integer id;
    private Long sede;
    private Integer tipoBloque;
    private String nombre;
    private String geolocalizacion;
    private String coordenadas;
    private Integer numeroPisos;
    private String descripcion;
    private Integer estado;
}