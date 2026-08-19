package com.inventario.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserRoleForbiddenException extends RuntimeException {

	private static final long serialVersionUID = -265882067333163971L;

	public UserRoleForbiddenException(String message) {
		super(message);
	}

}
