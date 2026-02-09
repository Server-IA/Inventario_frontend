package com.coagronet.exceptionHandler.custom;

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
     * Construye una nueva excepción configurada con los detalles del conflicto de
     * recursos.
     * <p>
     * Inicializa la respuesta con el estado <strong>409 Conflict</strong> y
     * establece el <code>ProblemDetail</code>
     * con un título descriptivo y un tipo de error específico (URI). Utiliza la
     * clave de mensaje <code>modulo.duplicado</code>
     * para la internacionalización, pasando el nombre del recurso conflictivo como
     * argumento.
     * </p>
     *
     * @param nombre valor del atributo o identificador que generó la duplicidad
     *               (ej. nombre de usuario, código de subsistema).
     *               Este valor se inyecta en el mensaje de error para dar contexto
     *               al cliente.
     */
    public RecursoDuplicadoException(String nombre) {
        super(
                HttpStatus.CONFLICT,
                ProblemDetail.forStatus(HttpStatus.CONFLICT),
                null,
                "modulo.duplicado",
                new Object[] { nombre });

        getBody().setTitle("Conflicto de Recursos");
        getBody().setType(URI.create("https://coagronet.com/errors/duplicate"));
    }
}
