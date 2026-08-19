/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoEstado.java
 Descripcion        : Enumeracion de estados para clasificar vencimientos de producto.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-10 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.dtos;

public enum ReporteVencimientoProductoEstado {

	VENCIDO,

	PROXIMO_A_VENCER,

	TODOS;

	public static ReporteVencimientoProductoEstado normalize(ReporteVencimientoProductoEstado estado) {
		return estado == null ? TODOS : estado;
	}

}
