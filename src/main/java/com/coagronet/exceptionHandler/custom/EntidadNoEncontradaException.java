package com.coagronet.exceptionHandler.custom;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class EntidadNoEncontradaException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    public EntidadNoEncontradaException(String nombreEntidad, Long id) {
        super(
                HttpStatus.UNPROCESSABLE_ENTITY,
                // 1. Creamos el ProblemDetail explícitamente
                ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY),
                null, // Causa (Throwable), null porque no hay excepción previa
                "entidad.no_encontrada", // Code para messages.properties
                new Object[] { nombreEntidad, id } // Argumentos
        );

        // 2. Personalizamos el ProblemDetail accediendo a getBody()
        getBody().setTitle("Referencia Inválida");
        getBody().setType(URI.create("https://coagronet.com/errors/not-found"));
    }
}
