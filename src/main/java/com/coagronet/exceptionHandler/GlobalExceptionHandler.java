package com.coagronet.exceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import io.jsonwebtoken.ExpiredJwtException;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex,
			WebRequest request) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "Validation Failed",
				"Uno o más campos no son válidos.", request.getDescription(false), fieldErrors);

		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorDetails> handleBadRequest(BadRequestException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "Bad Request", ex.getMessage(),
				request.getDescription(false), null);
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorDetails> handleNotFound(NotFoundException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "Not Found", ex.getMessage(),
				request.getDescription(false), null);
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ErrorDetails> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "Unauthorized", "El token JWT ha expirado.",
				request.getDescription(false), null);
		return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorDetails> handleDataIntegrityViolation(DataIntegrityViolationException ex,
			WebRequest request) {
		String message = "No se puede completar la operación porque existen datos relacionados o restricciones en la base de datos.";
		Throwable rootCause = ex.getRootCause();
		if (rootCause instanceof SQLException) {
			String sqlState = ((SQLException) rootCause).getSQLState();
			if ("23503".equals(sqlState)) {
				message = "No se puede eliminar o modificar el registro porque está siendo referenciado por otros datos (por ejemplo, departamentos asociados a un país).";
			}
		}
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "Data Integrity Violation", message,
				request.getDescription(false), null);

		return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT); // 409
	}

	@ExceptionHandler(UserRoleForbiddenException.class)
	public ResponseEntity<ErrorDetails> handleUserRoleForbidden(UserRoleForbiddenException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(
				LocalDateTime.now(),
				"Forbidden",
				ex.getMessage(),
				request.getDescription(false),
				null);
		return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
	}
}