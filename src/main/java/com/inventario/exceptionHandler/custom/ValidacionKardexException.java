package com.inventario.exceptionHandler.custom;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ValidacionKardexException extends ErrorResponseException {

    public ValidacionKardexException(String detalle) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, crearProblemDetail(detalle), null);
    }

    private static ProblemDetail crearProblemDetail(String detalle) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, detalle);
        body.setTitle("Error de Validación de Kardex");
        return body;
    }
}
