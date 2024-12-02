package com.coagronet.espacio.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EspacioDTO {
    private Integer id;
    private Integer bloque;
    private Integer tipoEspacio;
    private String nombre;
    private String geolocalizacion;
    private String coordenadas;
    private String descripcion;
    private Integer estado;
}
