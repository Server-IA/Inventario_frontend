package com.inventario.espacio.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EspacioDTO {

	private Long id;

	@NotNull(message = "El bloque es obligatorio.")
	private Long bloqueId;

	@NotNull(message = "El tipo de espacio es obligatorio.")
	private Long tipoEspacioId;

	@NotBlank(message = "El nombre es obligatorio.")
	@Size(max = 100, message = "El nombre no debe superar los 100 caracteres.")
	private String nombre;

	@Size(max = 255, message = "La descripcion no debe superar los 255 caracteres.")
	private String descripcion;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

	@Size(max = 255, message = "La geolocalizacion no debe superar los 255 caracteres.")
	private String geolocalizacion;

	@Size(max = 4096, message = "Las coordenadas no debe superar los 4096 caracteres.")
	private String coordenadas;

	@Size(max = 255, message = "La direccion no debe superar los 255 caracteres.")
	private String direccion;

	private Long empresaId;

}