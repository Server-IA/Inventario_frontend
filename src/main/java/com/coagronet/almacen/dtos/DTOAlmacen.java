package com.coagronet.almacen.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOAlmacen {
    private Integer id;
    private String nombre;
    private Long sede;
    private String geolocalizacion;
    private String coordenadas;
    private String descripcion;
    private Integer estado;
}
