package com.coagronet.reports.exceptions;

public class ReporteKardexPreloadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReporteKardexPreloadException(Throwable cause) {
		super("report.kardex.preload.error", cause);
	}
}
