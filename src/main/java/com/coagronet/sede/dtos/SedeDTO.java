package com.coagronet.sede.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SedeDTO {
	
	private Long id;
	
	@NotNull(message = "El grupo es obligatorio.")
	private Long grupoId;
	
	@NotNull(message = "El tipode sede es obligatorio.")
	private Long tipoSedeId;
	
	private Long empresaId;
	
	@Size(max = 100, message = "El nombre no debe superar los 100 caracteres.")
	private String nombre;
	
	@NotNull(message = "El municipio es obligatorio.")
	private Long municipioId;
	
	@Size(max = 70, message = "El geolocalizacion no debe superar los 70 caracteres.")
	private String geolocalizacion;
	
	@Size(max = 30, message = "Las coordenadas no debe superar los 30 caracteres.")
	private String coordenadas;
	
	private Double area;
	
	@Size(max = 100, message = "La comuna no debe superar los 100 caracteres.")
	private String comuna;
	
	@Size(max = 255, message = "La descripcion no debe superar los 255 caracteres.")
	private String descripcion;
	
	@Size(max = 255, message = "La direccion no debe superar los 255 caracteres.")
	private String direccion;
	
	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;
}