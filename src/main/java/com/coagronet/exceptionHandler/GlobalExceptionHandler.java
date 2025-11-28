package com.coagronet.exceptionHandler;

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
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final MessageSource messageSource;

	private String msg(String codeOrRaw, Object[] args, Locale locale) {
		return messageSource.getMessage(codeOrRaw, args, codeOrRaw, locale);
	}

	private String requestPath(WebRequest request) {
		if (request instanceof ServletWebRequest swr) {
			return swr.getRequest().getRequestURI();
		}
		// WebRequest.getDescription(false) -> "uri=/path"
		String desc = request.getDescription(false);
		return (desc != null && desc.startsWith("uri=")) ? desc.substring(4) : desc;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex,
			WebRequest request) {
		Locale locale = LocaleContextHolder.getLocale();

		Map<String, String> fieldErrors = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(fe -> {
			String m = messageSource.getMessage(fe, locale);
			fieldErrors.put(fe.getField(), m);
		});

		ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Validation Failed",
				msg("error.validation", null, locale), requestPath(request), fieldErrors);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorDetails> handleBadRequest(BadRequestException ex, WebRequest request) {
		Locale locale = LocaleContextHolder.getLocale();
		ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Bad Request",
				msg(ex.getMessage() != null ? ex.getMessage() : "error.bad-request", ex.getArgs(), locale),
				requestPath(request), null);
		return ResponseEntity.badRequest().body(body);
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
		String code = "db.integrity";
		Throwable root = ex.getRootCause();
		if (root instanceof SQLException sql && "23503".equals(sql.getSQLState())) {
			code = "db.integrity.fk";
		}
		ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Data Integrity Violation", msg(code, null, locale),
				requestPath(request), null);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(body); // 409
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

		ErrorDetails body = new ErrorDetails(
				LocalDateTime.now(),
				"Not Found",
				// msg() usa codeOrRaw y si no existe en messages.properties devuelve el mismo
				// texto
				msg(ex.getMessage() != null ? ex.getMessage() : "error.not-found", null, locale),
				requestPath(request),
				null);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorDetails> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
		Locale locale = LocaleContextHolder.getLocale();

		ErrorDetails body = new ErrorDetails(
				LocalDateTime.now(),
				"Bad Request",
				msg(ex.getMessage() != null ? ex.getMessage() : "error.bad-request", null, locale),
				requestPath(request),
				null);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorDetails> handleIllegalState(IllegalStateException ex, WebRequest request) {
		Locale locale = LocaleContextHolder.getLocale();

		ErrorDetails body = new ErrorDetails(
				LocalDateTime.now(),
				"Conflict",
				msg(ex.getMessage() != null ? ex.getMessage() : "error.conflict", null, locale),
				requestPath(request),
				null);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

}
