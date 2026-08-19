/*=============================================================================
 Nombre del archivo : GlobalExceptionHandler.java
 Descripcion        : Manejador global de excepciones en proceso de migracion.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2024-10-21 | 1.0.0   | jujcgu               | Creacion del archivo.                                                                                                              |
 | 2026-05-27 | 1.1.0   | JUAN DIAZ            | Refactor de catalogos globales: ajustes en entidades, DTOs, mappers, repositorios y servicios, con validaciones de negocio.        |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.exceptionHandler;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
@Deprecated(since = "2026-02-08", forRemoval = true) // Migración a Advice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private String msg(String codeOrRaw, Object[] args, Locale locale) {
        return messageSource.getMessage(codeOrRaw, args, codeOrRaw, locale);
    }

    private static final Map<String, String> CONSTRAINT_MAP = Map.ofEntries(
            Map.entry("unique_emp_correo", "empresa.correo.existente"),
            Map.entry("unique_emp_identificacion", "empresa.identificacion.existente"),
            Map.entry("uq_pais_nombre", "country.name.duplicate"),
            Map.entry("uq_pais_codigo", "country.code.duplicate"),
            Map.entry("uq_pais_acronimo", "country.acronym.duplicate"),
            Map.entry("uq_departamento_pais_nombre", "department.name.duplicate"),
            Map.entry("uq_departamento_pais_codigo", "department.code.duplicate"),
            Map.entry("uq_departamento_pais_acronimo", "department.acronym.duplicate"),
            Map.entry("uq_municipio_departamento_nombre", "municipality.name.duplicate"),
            Map.entry("uq_municipio_departamento_codigo", "municipality.code.duplicate"),
            Map.entry("uq_municipio_departamento_acronimo", "municipality.acronym.duplicate"));

    private String requestPath(WebRequest request) {
        if (request instanceof ServletWebRequest swr) {
            return swr.getRequest().getRequestURI();
        }
        // WebRequest.getDescription(false) -> "uri=/path"
        String desc = request.getDescription(false);
        return (desc != null && desc.startsWith("uri=")) ? desc.substring(4) : desc;
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ErrorDetails> handleDomainValidation(DomainValidationException ex, WebRequest request) {
        var locale = LocaleContextHolder.getLocale();
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (var v : ex.getViolations()) {
            String resolved = messageSource.getMessage(v.getCode(), v.getArgs(), v.getCode(), locale);
            fieldErrors.put(v.getField(), resolved);
        }

        ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Validation Failed",
                msg("error.validation", null, locale), requestPath(request), fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFound(NotFoundException ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Not Found",
                msg(ex.getCode() != null ? ex.getCode() : "error.not-found", ex.getArgs(), locale),
                requestPath(request), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorDetails> handleExpiredJwt(ExpiredJwtException ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Unauthorized", msg("jwt.expired", null, locale),
                requestPath(request), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String code = "db.integrity"; // Código por defecto
        Throwable root = ex.getRootCause();

        if (root instanceof SQLException sql) {
            String message = sql.getMessage();
            String sqlState = sql.getSQLState();

            // 2. Buscamos en el mapa si alguna de nuestras llaves está en el mensaje de SQL
            if (message != null) {
                code = CONSTRAINT_MAP.entrySet().stream()
                        .filter(entry -> message.contains(entry.getKey()))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(code); // Si no encuentra nada en el mapa, mantiene el default

                // 3. Fallback para llaves foráneas si no fue un error de duplicidad
                if (code.equals("db.integrity") && "23503".equals(sqlState)) {
                    code = "db.integrity.fk";
                }
            }
        }

        ErrorDetails body = new ErrorDetails(
                LocalDateTime.now(),
                "Data Integrity Violation",
                msg(code, null, locale),
                requestPath(request),
                null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(UserRoleForbiddenException.class)
    public ResponseEntity<ErrorDetails> handleForbidden(UserRoleForbiddenException ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Forbidden",
                msg(ex.getMessage() != null ? ex.getMessage() : "error.forbidden", null, locale), requestPath(request),
                null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler({ NoResourceFoundException.class, NoHandlerFoundException.class })
    public ResponseEntity<ErrorDetails> handleNoResource(Exception ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Not Found",
                msg("error.endpoint-not-found", null, locale), requestPath(request), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDetails> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
            WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        var headers = new org.springframework.http.HttpHeaders();
        if (ex.getSupportedHttpMethods() != null)
            headers.setAllow(ex.getSupportedHttpMethods());

        String message = ex.getSupportedHttpMethods() != null
                ? msg("error.method-not-allowed.with-allow", new Object[] { ex.getSupportedHttpMethods() }, locale)
                : msg("error.method-not-allowed", null, locale);

        ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Method Not Allowed", message, requestPath(request),
                null);
        return new ResponseEntity<>(body, headers, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGeneric(Exception ex, WebRequest request) {
        System.err.println("Unhandled exception at " + requestPath(request));
        ex.printStackTrace(System.err);

        Locale locale = LocaleContextHolder.getLocale();
        ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Internal Server Error",
                msg(ex.getMessage(), null, locale), requestPath(request), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        String message = "Falta el parámetro requerido '" + ex.getParameterName() + "'";
        if ("tipoAplicacion".equals(ex.getParameterName())) {
            message += " (valores válidos: web | movil)";
        }

        Map<String, Object> body = Map.of("timestamp", Instant.now().toString(), "path", req.getRequestURI(), "code",
                "VALIDATION_ERROR", "message", message, "fieldErrors",
                List.of(Map.of("field", ex.getParameterName(), "message", "required")));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();

        ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Not Found",
                // msg() usa codeOrRaw y si no existe en messages.properties devuelve el mismo
                // texto
                msg(ex.getMessage() != null ? ex.getMessage() : "error.not-found", null, locale), requestPath(request),
                null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();

        ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Bad Request",
                msg(ex.getMessage() != null ? ex.getMessage() : "error.bad-request", null, locale),
                requestPath(request), null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDetails> handleIllegalState(IllegalStateException ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();

        ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Conflict",
                msg(ex.getMessage() != null ? ex.getMessage() : "error.conflict", null, locale), requestPath(request),
                null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleDisabled(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Usuario inactivo o deshabilitado"));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleRse(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", ex.getReason()));
    }

}









