/*=============================================================================
 Nombre del archivo : ReportePedidoException.java
 Descripcion        : Excepcion de dominio para la consulta y exportacion del reporte de pedido.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-18 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.reports.exceptions;

import org.springframework.http.HttpStatus;

public class ReportePedidoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final HttpStatus status;

	public ReportePedidoException(String messageKey, HttpStatus status) {
		super(messageKey);
		this.status = status;
	}

	public ReportePedidoException(String messageKey, HttpStatus status, Throwable cause) {
		super(messageKey, cause);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

}
