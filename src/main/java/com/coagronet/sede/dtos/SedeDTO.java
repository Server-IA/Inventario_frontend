package com.coagronet.sede.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SedeDTO {
    private Long id;
    private Long grupoId;
    private Integer tipoSedeId;
    private Long empresaId;
    private String nombre;
    private Integer municipioId;
    private Double area;
    private String comuna;
    private String descripcion;
    private Integer estadoId;
    private String geolocalizacion;
    private String coordenadas;
    private String direccion;
}
