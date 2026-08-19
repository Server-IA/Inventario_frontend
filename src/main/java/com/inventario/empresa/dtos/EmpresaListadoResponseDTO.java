/*=============================================================================
 Nombre del archivo : EmpresaListadoResponseDTO.java
 Descripcion        : DTO de respuesta paginada para el listado de empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion del archivo para conservar el contrato header y data de la HU-043.2.                                                     |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.empresa.dtos;

import java.util.List;

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
public class EmpresaListadoResponseDTO {

	private Paginacion header;

	private List<EmpresaListadoItemDTO> data;

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Paginacion {

		private long totalElements;

		private int totalPages;

		private int size;

		private int number;

		private boolean first;

		private boolean last;

		private int numberOfElements;

		private boolean empty;

	}

}
