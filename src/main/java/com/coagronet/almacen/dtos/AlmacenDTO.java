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
    private Long sedeId;
    private String descripcion;
    private Integer estadoId;
    private String geolocalizacion;
    private String coordenadas;
    private Integer espacioId;
    private String direccion;
}
