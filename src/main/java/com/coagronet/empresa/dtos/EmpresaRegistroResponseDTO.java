/*=============================================================================
 Nombre del archivo : EmpresaRegistroResponseDTO.java
 Descripcion        : DTO de respuesta para el registro de empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion del archivo para informar la empresa registrada y el resultado de la carga del logo.                                     |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.empresa.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class EmpresaRegistroResponseDTO {

	private Long id;

	private Long tipoIdentificacionId;

	private String identificacion;

	private String nombre;

	private String correo;

	private String celular;

	private String contacto;

	private String descripcion;

	private Long personaId;

	private Long estadoId;

	private String logo;

	private boolean logoCargado;

	private String advertenciaLogo;

}
