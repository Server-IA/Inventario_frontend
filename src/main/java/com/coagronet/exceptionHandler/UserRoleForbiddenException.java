package com.coagronet.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserRoleForbiddenException extends RuntimeException {
    public UserRoleForbiddenException(String message) {
        super(message);
    }
}
