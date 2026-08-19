/*=============================================================================
 Nombre del archivo : EmpresaDetalleResponseDTO.java
 Descripcion        : DTO de respuesta con el detalle completo de una empresa.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion del archivo para la visualizacion del detalle de empresa de la HU-043.3.                                                  |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.empresa.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDetalleResponseDTO {

	private Long id;

	private Long tipoIdentificacionId;

	private String tipoIdentificacionNombre;

	private String identificacion;

	private String nombre;

	private String correo;

	private String celular;

	private String contacto;

	private String descripcion;

	private String logo;

	private Long estadoId;

	private String estadoNombre;

	private Long personaResponsableId;

	private String personaResponsableNombre;

}
