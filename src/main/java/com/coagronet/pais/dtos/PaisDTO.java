/*=============================================================================
 Nombre del archivo : PaisDTO.java
 Descripcion        : DTO para intercambio de datos de paises.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2025-03-31 | 1.0.0   | jujcgu               | Creacion del archivo.                                                                                                              |
 | 2026-05-27 | 1.1.0   | JUAN DIAZ            | Refactor de catalogos globales: ajustes en entidades, DTOs, mappers, repositorios y servicios, con validaciones de negocio.        |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.pais.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaisDTO {

	private Long id;

	@NotBlank(message = "{country.name.required}")
	@Size(max = 100, message = "{country.name.max-size}")
	private String nombre;

	@NotNull(message = "{country.code.required}")
	@Positive(message = "{country.code.positive}")
	private Long codigo;

	@NotBlank(message = "{country.acronym.required}")
	@Size(max = 3, message = "{country.acronym.size}")
	@Pattern(regexp = "^[A-Za-z]{1,3}$", message = "{country.acronym.only-letters}")
	private String acronimo;

	@NotNull(message = "{country.status.required}")
	private Long estadoId;

}









