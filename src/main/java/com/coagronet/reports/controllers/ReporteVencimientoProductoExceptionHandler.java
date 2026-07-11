package com.coagronet.reports.controllers;

import java.time.Instant;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.coagronet.reports.exceptions.ReporteVencimientoProductoException;

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
