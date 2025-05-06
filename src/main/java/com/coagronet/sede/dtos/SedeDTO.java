package com.coagronet.sede.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SedeDTO {
	private Long id;
	private Long grupo;
	private Integer tipoSede;
	private Long empresa;
	private String nombre;
	private Integer municipio;
	private String geolocalizacion;
	private String coordenadas;
	private Double area;
	private String comuna;
	private String descripcion;
	private String direccion;
	private Integer estado;
}