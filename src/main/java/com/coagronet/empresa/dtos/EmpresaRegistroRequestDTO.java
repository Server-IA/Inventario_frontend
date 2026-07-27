/*=============================================================================
 Nombre del archivo : EmpresaRegistroRequestDTO.java
 Descripcion        : DTO de entrada para registrar empresas en el sistema.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion del archivo para implementar la HU-043.1 de registro de empresas.                                                         |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.empresa.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmpresaRegistroRequestDTO {

	@NotNull(message = "{empresa.tipo-identificacion.required}")
	@Positive(message = "{empresa.tipo-identificacion.positive}")
	private Long tipoIdentificacionId;

	@NotBlank(message = "{empresa.identificacion.required}")
	@Size(max = 50, message = "{empresa.identificacion.max-size}")
	private String identificacion;

	@NotBlank(message = "{empresa.nombre.required}")
	@Size(max = 100, message = "{empresa.nombre.max-size}")
	private String nombre;

	@NotBlank(message = "{empresa.correo.required}")
	@Email(message = "{empresa.correo.invalid}")
	@Size(max = 255, message = "{empresa.correo.max-size}")
	private String correo;

	@Size(max = 13, message = "{empresa.celular.max-size}")
	private String celular;

	@Size(max = 255, message = "{empresa.contacto.max-size}")
	private String contacto;

	@Size(max = 2048, message = "{empresa.descripcion.max-size}")
	private String descripcion;

	@NotNull(message = "{empresa.persona-responsable.required}")
	@Positive(message = "{empresa.persona-responsable.positive}")
	private Long personaId;

}
