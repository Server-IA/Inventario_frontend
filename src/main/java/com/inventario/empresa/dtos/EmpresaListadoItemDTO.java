/*=============================================================================
 Nombre del archivo : EmpresaListadoItemDTO.java
 Descripcion        : DTO resumido de una empresa dentro del listado.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion del archivo con los datos generales requeridos por la HU-043.2.                                                          |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.empresa.dtos;

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
public class EmpresaListadoItemDTO {

	private Long id;

	private Long tipoIdentificacionId;

	private String tipoIdentificacionNombre;

	private String identificacion;

	private String nombre;

	private String correo;

	private Long estadoId;

	private String estadoNombre;

}
