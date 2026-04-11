package com.coagronet.exceptionHandler.custom;

import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ProductoSinResponsableException extends ErrorResponseException {

	public ProductoSinResponsableException(String mensaje) {
		super(HttpStatus.UNPROCESSABLE_ENTITY, construirProblemDetail(mensaje), null);
	}

	private static ProblemDetail construirProblemDetail(String mensaje) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, mensaje);

		problemDetail.setTitle("Validación de Movimiento de Inventario");
		problemDetail.setType(URI.create("https://coagronet.com/errors/business-validation"));
		problemDetail.setProperty("timestamp", Instant.now());
		problemDetail.setProperty("module", "Kardex");
		return problemDetail;
	}

}
