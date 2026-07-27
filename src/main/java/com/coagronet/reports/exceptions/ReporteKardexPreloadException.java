/*=============================================================================
 Nombre del archivo : ReporteKardexPreloadException.java
 Descripcion        : Excepcion de dominio para la precarga del reporte Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-06-19 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.exceptions;

public class ReporteKardexPreloadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReporteKardexPreloadException(Throwable cause) {
		super("report.kardex.preload.error", cause);
	}
}
