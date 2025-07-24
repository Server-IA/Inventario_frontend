package com.coagronet.exceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {

	private LocalDateTime timestamp;

	private String error;

	private String message;

	private String path;

	private Map<String, String> fieldErrors;

}