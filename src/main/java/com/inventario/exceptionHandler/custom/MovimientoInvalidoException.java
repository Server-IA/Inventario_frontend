package com.inventario.exceptionHandler.custom;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class MovimientoInvalidoException extends ErrorResponseException {

	private static final long serialVersionUID = 1L;

	public MovimientoInvalidoException(Long kardexId) {
		super(HttpStatus.CONFLICT, ProblemDetail.forStatus(HttpStatus.CONFLICT), null, "kardex.operacion_invalida",
				new Object[] { kardexId });

		getBody().setTitle("Operación Inválida");
		getBody().setType(URI.create("https://inmero.co/inventario/errors/invalid-kardex-operation"));
	}

}
