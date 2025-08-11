package com.coagronet.bloque.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Min;
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
public class BloqueDTO {

	private Long id;

	@NotNull(message = "La sede es obligatoria.")
	private Long sedeId;

	@NotNull(message = "El tipo de bloque es obligatorio.")
	private Long tipoBloqueId;

	@NotBlank(message = "El nombre es obligatorio.")
	@Size(max = 100, message = "El nombre no debe superar los 100 caracteres.")
	private String nombre;

	@NotNull(message = "El n�mero de pisos es obligatorio.")
	@Min(value = 1, message = "El n�mero de pisos debe ser al menos 1.")
	private Integer numeroPisos;

	@Size(max = 255, message = "La descripci�n no debe superar los 255 caracteres.")
	private String descripcion;

	@NotNull(message = "El estado es obligatorio.")
	private Long estadoId;

	@Size(max = 255, message = "La geolocalizaci�n no debe superar los 255 caracteres.")
	private String geolocalizacion;

	@Size(max = 255, message = "Las coordenadas no deben superar los 255 caracteres.")
	private String coordenadas;

	@Size(max = 255, message = "La direcci�n no debe superar los 255 caracteres.")
	private String direccion;

	private Long empresaId;

}