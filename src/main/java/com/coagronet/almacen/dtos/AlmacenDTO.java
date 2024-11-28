package com.coagronet.almacen.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlmacenDTO {
    private Integer id;
    private String nombre;
    private Long sede;
    private String geolocalizacion;
    private String coordenadas;
    private String descripcion;
    private Integer estado;
}
