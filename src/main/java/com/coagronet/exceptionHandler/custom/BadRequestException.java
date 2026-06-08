package com.coagronet.exceptionHandler.custom;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.util.Map;

public class BadRequestException extends ErrorResponseException {

	public BadRequestException(String detail) {
		super(
				HttpStatus.BAD_REQUEST,
				ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail),
				null);
		this.getBody().setType(URI.create("https://coagronet.com/errors/bad-request"));
	}

	public BadRequestException(String detail, Map<String, String> invalidParams) {
		this(detail);

		if (invalidParams != null && !invalidParams.isEmpty()) {
			this.getBody().setProperty("invalid_params", invalidParams);
		}
	}
}