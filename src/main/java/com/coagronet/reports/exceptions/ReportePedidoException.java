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
