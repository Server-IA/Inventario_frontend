/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoExceptionHandler.java
 Descripcion        : Manejador de excepciones del reporte de vencimiento de producto.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-10 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.controllers;

import java.time.Instant;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.inventario.reports.exceptions.ReporteVencimientoProductoException;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice(assignableTypes = ReporteVencimientoProductoController.class)
@RequiredArgsConstructor
public class ReporteVencimientoProductoExceptionHandler {

	private final MessageSource messageSource;

	@ExceptionHandler(ReporteVencimientoProductoException.class)
	public ProblemDetail handleReporteVencimientoProductoException(
			ReporteVencimientoProductoException exception,
			Locale locale) {
		String detail = messageSource.getMessage(
				exception.getMessage(),
				null,
				"No fue posible procesar el reporte de vencimiento de producto.",
				locale);
		String title = messageSource.getMessage(
				"report.vencimiento.error.title",
				null,
				"Error de reporte",
				locale);

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getStatus(), detail);
		problemDetail.setTitle(title);
		problemDetail.setProperty("timestamp", Instant.now());
		return problemDetail;
	}

}
