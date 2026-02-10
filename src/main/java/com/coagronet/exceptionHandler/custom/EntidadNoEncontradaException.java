package com.coagronet.exceptionHandler.custom;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * Excepción específica de dominio lanzada cuando una entidad requerida no puede
 * ser hallada en la capa de persistencia.
 * <p>
 * Esta clase extiende {@link ErrorResponseException} para proporcionar una
 * estructura de error compatible con el
 * estándar <strong>RFC 7807 (Problem Details)</strong>. Se mapea al código de
 * estado HTTP <code>422 UNPROCESSABLE_ENTITY</code>,
 * indicando que, aunque la solicitud es sintácticamente correcta, contiene
 * instrucciones semánticas erróneas
 * (por ejemplo, una referencia a una llave foránea inexistente).
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see ErrorResponseException
 * @see ProblemDetail
 * @since 2026
 */
public class EntidadNoEncontradaException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de la excepción con los metadatos de la entidad
     * faltante.
     * <p>
     * Inicializa el cuerpo del error con un título estandarizado ("Referencia
     * Inválida") y un URI de tipo específico
     * para la plataforma Coagronet. Utiliza el código de mensaje
     * <code>entidad.no_encontrada</code> para permitir
     * la internacionalización (i18n) de la respuesta, inyectando el nombre de la
     * entidad y el ID como argumentos.
     * </p>
     *
     * @param nombreEntidad nombre técnico o funcional de la entidad de negocio (ej.
     *                      "SubSistema", "Usuario").
     *                      Se utiliza para contextualizar el mensaje de error.
     * @param id            identificador único (llave primaria) que se intentó
     *                      buscar sin éxito.
     */
    public EntidadNoEncontradaException(String nombreEntidad, Long id) {
        super(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY),
                null,
                "entidad.no_encontrada",
                new Object[] { nombreEntidad, id });

        getBody().setTitle("Referencia Inválida");
        getBody().setType(URI.create("https://coagronet.com/errors/not-found"));
    }
}
