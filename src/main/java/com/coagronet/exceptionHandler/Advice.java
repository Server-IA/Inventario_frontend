package com.coagronet.exceptionHandler;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * Provee el manejo centralizado de excepciones para la API REST, interceptando errores específicos y estandarizando las
 * respuestas HTTP.
 * <p>
 * Esta clase utiliza la anotación {@link RestControllerAdvice} para aplicar lógica transversal de manejo de errores en
 * todos los controladores de la aplicación. Extiende de {@link ResponseEntityExceptionHandler} para heredar y
 * personalizar el comportamiento base de las excepciones MVC de Spring.
 * </p>
 * <p>
 * Su objetivo principal es transformar las excepciones de validación en estructuras de respuesta consistentes (RFC 7807
 * Problem Details), facilitando al cliente el consumo y entendimiento de los errores de entrada.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see RestControllerAdvice
 * @see ResponseEntityExceptionHandler
 * @since 2026
 */
@RestControllerAdvice
public class Advice extends ResponseEntityExceptionHandler {

    /**
     * Personaliza la respuesta cuando falla la validación de un argumento anotado con <code>@Valid</code> en el cuerpo
     * de la petición.
     * <p>
     * Este método sobrescribe el comportamiento por defecto para capturar los errores de campo (Field Errors),
     * recolectarlos en un mapa y adjuntarlos a la propiedad extendida "errors" del objeto <code>ProblemDetail</code>.
     * </p>
     * <p>
     * Además, establece un tipo de error (URI) y un título estándar para identificar problemas de validación en la
     * plataforma Coagronet. En caso de múltiples errores en un mismo campo, los mensajes se concatenan separados por
     * punto y coma.
     * </p>
     *
     * @param ex la excepción lanzada que contiene los resultados del binding y la lista de errores de validación.
     * @param headers los encabezados HTTP que se escribirán en la respuesta.
     * @param status el código de estado HTTP seleccionado (usualmente 400 Bad Request).
     * @param request la solicitud web actual durante la cual se lanzó la excepción.
     * @return una instancia de {@link ResponseEntity} que contiene el cuerpo de la respuesta con los detalles del
     * problema y el mapa de errores de validación específicos.
     * @see MethodArgumentNotValidException
     * @see org.springframework.http.ProblemDetail
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        var problemDetail = ex.getBody();

        // Recolecta los errores de campo en un mapa simple (Campo -> Mensaje)
        Map<String, String> errores = ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(
                error -> error.getField(), error -> error.getDefaultMessage(), (msg1, msg2) -> msg1 + "; " + msg2)); // Manejo
                                                                                                                     // de
                                                                                                                     // colisiones
                                                                                                                     // en
                                                                                                                     // claves
                                                                                                                     // duplicadas

        problemDetail.setProperty("errors", errores);

        problemDetail.setType(URI.create("https://coagronet.com/errors/validation"));
        problemDetail.setTitle("Error de Validación");

        return createResponseEntity(problemDetail, headers, status, request);
    }

    /**
     * Intercepta y gestiona la excepción lanzada cuando un token de seguridad JWT ha superado su tiempo de validez.
     * <p>
     * Este método captura {@link ExpiredJwtException} para transformar el error técnico en una respuesta estructurada
     * (RFC 7807). Mapea el incidente al código de estado HTTP <code>401 UNAUTHORIZED</code> y define un mensaje
     * amigable ("El token de sesión ha caducado...") para guiar al usuario final hacia un nuevo inicio de sesión.
     * </p>
     * <p>
     * Además, categoriza el error con el URI <code>.../errors/jwt-expired</code> para facilitar su identificación
     * programática en el cliente.
     * </p>
     *
     * @param ex la excepción capturada que contiene los metadatos del token vencido.
     * @return una instancia de {@link ProblemDetail} configurada con el título "Token Expirado" y las instrucciones de
     * remediación.
     * @see ExpiredJwtException
     * @see ProblemDetail
     * @since 2026
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(ExpiredJwtException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());

        problemDetail.setTitle("Token Expirado");
        problemDetail.setType(URI.create("https://coagronet.com/errors/jwt-expired"));

        problemDetail.setDetail("El token de sesión ha caducado. Por favor, inicie sesión nuevamente.");

        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex, Locale locale) {

        String mensaje = getMessageSource() != null
                ? getMessageSource().getMessage("security.access_denied", null, "Acceso denegado", locale)
                : "No tiene permisos para acceder a este recurso.";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, mensaje);
        problemDetail.setTitle("Acceso Prohibido");
        problemDetail.setType(URI.create("https://coagronet.com/errors/forbidden"));

        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex, Locale locale) {

        String mensaje = getMessageSource() != null
                ? getMessageSource().getMessage("security.unauthorized", null, "Se requiere autenticación completa.",
                        locale)
                : "No se proporcionaron credenciales válidas.";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, mensaje);
        problemDetail.setTitle("No Autenticado");
        problemDetail.setType(URI.create("https://coagronet.com/errors/unauthorized"));

        return problemDetail;
    }

}
