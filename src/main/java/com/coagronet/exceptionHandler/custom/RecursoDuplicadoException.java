package com.coagronet.exceptionHandler.custom;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class RecursoDuplicadoException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    public RecursoDuplicadoException(String nombre) {
        super(
                HttpStatus.CONFLICT,
                ProblemDetail.forStatus(HttpStatus.CONFLICT), // 1. Corrección: Crear el detalle explícitamente
                null,
                "modulo.duplicado", // Clave en messages.properties
                new Object[] { nombre } // Argumentos para el mensaje
        );

        // 2. Corrección: Acceder al cuerpo (ProblemDetail) para configurar título y
        // tipo
        getBody().setTitle("Conflicto de Recursos");
        getBody().setType(URI.create("https://coagronet.com/errors/duplicate"));
    }
}
