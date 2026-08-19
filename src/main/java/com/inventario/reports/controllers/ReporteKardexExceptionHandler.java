/*=============================================================================
 Nombre del archivo : ReporteKardexExceptionHandler.java
 Descripcion        : Manejador de excepciones para la precarga del reporte Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-06-19 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.controllers;

import java.time.Instant;
import java.util.Locale;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.inventario.reports.exceptions.ReporteKardexPreloadException;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice(assignableTypes = ReporteKardexPreloadController.class)
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReporteKardexExceptionHandler {

	private final MessageSource messageSource;

	@ExceptionHandler(ReporteKardexPreloadException.class)
	public ProblemDetail handlePreloadException(ReporteKardexPreloadException exception, Locale locale) {
		String detail = messageSource.getMessage(
				exception.getMessage(),
				null,
				"No fue posible precargar los filtros del reporte Kardex.",
				locale);
		String title = messageSource.getMessage(
				"report.kardex.preload.error.title",
				null,
				"Error de precarga",
				locale);

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, detail);
		problemDetail.setTitle(title);
		problemDetail.setProperty("timestamp", Instant.now());
		return problemDetail;
	}
}
