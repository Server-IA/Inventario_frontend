package com.inventario.exceptionHandler.custom;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * Excepción lanzada cuando se intenta crear o modificar un recurso que viola
 * una restricción de unicidad en el sistema.
 * <p>
 * Esta clase extiende {@link ErrorResponseException} para mapear errores de
 * integridad de datos (como llaves duplicadas)
 * al código de estado HTTP <code>409 CONFLICT</code>. Implementa la
 * especificación <strong>RFC 7807</strong> para
 * detallar la naturaleza del conflicto al cliente de la API.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see ErrorResponseException
 * @see HttpStatus#CONFLICT
 * @since 2026
 */
public class RecursoDuplicadoException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva excepción usando un mensaje de detalle directo.
     * * @param mensajeDetalle El mensaje exacto que se le mostrará al cliente
     * (ej. "El username ya se encuentra registrado.")
     */
    public RecursoDuplicadoException(String mensajeDetalle) {
        // Usamos forStatusAndDetail para inyectar el mensaje directamente
        super(
                HttpStatus.CONFLICT,
                ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, mensajeDetalle),
                null);

        getBody().setTitle("Conflicto de Recursos");
        getBody().setType(URI.create("https://inmero.co/inventario/errors/duplicate"));
    }
}
