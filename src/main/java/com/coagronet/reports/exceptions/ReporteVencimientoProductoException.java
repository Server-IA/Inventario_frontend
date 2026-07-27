/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoException.java
 Descripcion        : Excepcion de dominio del reporte de vencimiento de producto.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-10 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.exceptions;

import org.springframework.http.HttpStatus;

public class ReporteVencimientoProductoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final HttpStatus status;

	public ReporteVencimientoProductoException(String messageKey, HttpStatus status) {
		super(messageKey);
		this.status = status;
	}

	public ReporteVencimientoProductoException(String messageKey, HttpStatus status, Throwable cause) {
		super(messageKey, cause);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

}
