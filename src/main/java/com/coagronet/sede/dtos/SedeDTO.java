package com.coagronet.sede.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SedeDTO {
	private Long id;
	private Long grupo;
	private Long tipoSede;
	private Long empresa;
	private String nombre;
	private Long municipio;
	private String geolocalizacion;
	private String coordenadas;
	private Double area;
	private String comuna;
	private String descripcion;
	private String direccion;
	private Long estado;
}