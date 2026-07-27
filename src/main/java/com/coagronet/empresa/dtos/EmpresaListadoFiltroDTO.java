/*=============================================================================
 Nombre del archivo : EmpresaListadoFiltroDTO.java
 Descripcion        : DTO de filtros para consultar el listado de empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion del archivo para implementar los filtros de la HU-043.2.                                                                 |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.empresa.dtos;

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
public class EmpresaListadoFiltroDTO {

	@Positive(message = "{empresa.filtro.tipo-identificacion.positive}")
	private Long tipoIdentificacionId;

	@Size(max = 50, message = "{empresa.filtro.identificacion.max-size}")
	private String identificacion;

	@Size(max = 100, message = "{empresa.filtro.nombre.max-size}")
	private String nombre;

	@Size(max = 255, message = "{empresa.filtro.correo.max-size}")
	private String correo;

	@Positive(message = "{empresa.filtro.estado.positive}")
	private Long estadoId;

}
