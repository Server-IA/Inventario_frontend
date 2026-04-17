package com.coagronet.exceptionHandler.custom;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ReglaNegocioItemException extends ErrorResponseException {

	public ReglaNegocioItemException(String defaultMessage, String messageKey) {
		super(HttpStatus.UNPROCESSABLE_ENTITY, asProblemDetail(defaultMessage, messageKey), null, messageKey, null);
	}

	private static ProblemDetail asProblemDetail(String defaultMessage, String messageKey) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, defaultMessage);
		problemDetail.setTitle("Regla de negocio no cumplida");
		problemDetail.setType(URI.create("https://coagronet.com/errors/kardex-item-invalido"));

		problemDetail.setProperty("message_key", messageKey);

		return problemDetail;
	}

}
