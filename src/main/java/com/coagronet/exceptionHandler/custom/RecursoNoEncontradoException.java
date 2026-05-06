package com.coagronet.exceptionHandler.custom;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * Excepción lanzada cuando una solicitud HTTP intenta acceder a un recurso que
 * no existe en el servidor.
 * <p>
 * Esta clase extiende {@link ErrorResponseException} para mapear la ausencia de
 * datos al código de estado
 * <strong>404 NOT FOUND</strong>. Implementa la especificación <strong>RFC 7807
 * (Problem Details)</strong>,
 * proporcionando una estructura estandarizada para reportar errores de
 * navegación o consulta a los clientes de la API.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see ErrorResponseException
 * @see HttpStatus#NOT_FOUND
 * @since 2026
 */
public class RecursoNoEncontradoException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva excepción configurada con los detalles del recurso
     * ausente.
     * <p>
     * Inicializa el objeto {@link ProblemDetail} con el estado 404 y un título
     * descriptivo ("Recurso No Encontrado").
     * Utiliza la clave de internacionalización <code>recurso.no_encontrado</code> e
     * inyecta el nombre de la entidad
     * y su identificador en el mensaje de error, facilitando la depuración por
     * parte del cliente.
     * </p>
     *
     * @param nombreEntidad nombre técnico o funcional del tipo de recurso buscado
     *                      (ej. "Modulo", "Usuario").
     * @param id            identificador único (llave primaria) utilizado en la
     *                      búsqueda fallida.
     */
    public RecursoNoEncontradoException(String nombreEntidad, Object id) {
        super(
                HttpStatus.NOT_FOUND,
                ProblemDetail.forStatus(HttpStatus.NOT_FOUND),
                null,
                "recurso.no_encontrado",
                new Object[] { nombreEntidad, id });

        getBody().setTitle("Recurso No Encontrado");
        getBody().setType(URI.create("https://coagronet.com/errors/resource-not-found"));
    }
}
