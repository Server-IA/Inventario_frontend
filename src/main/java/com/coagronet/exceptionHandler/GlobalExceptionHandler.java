package com.coagronet.exceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

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
			// Resuelve mensaje de validaci?n (usa codes de Bean Validation o tu
			// message="{mi.key}")
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
				msg(ex.getMessage() != null ? ex.getMessage() : "error.bad-request", null, locale),
				requestPath(request), null);
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

	// fallback opcional
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> handleGeneric(Exception ex, WebRequest request) {
		Locale locale = LocaleContextHolder.getLocale();
		ErrorDetails body = new ErrorDetails(LocalDateTime.now(), "Internal Server Error",
				msg("error.internal", null, locale), // agrega key en messages si quieres
				requestPath(request), null);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}

}
