package com.coagronet.exceptionHandler;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String code;

	private final Object[] args;

	public NotFoundException(String code, Object... args) {
		super(code);
		this.code = code;
		this.args = args;
	}

	public String getCode() {
		return code;
	}

	public Object[] getArgs() {
		return args;
	}

}
