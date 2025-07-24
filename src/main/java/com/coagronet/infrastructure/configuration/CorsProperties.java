package com.coagronet.infrastructure.configuration;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

	/**
	 * Dominios confiables que pueden consumir la API.
	 */
	private List<String> allowedOrigins = List.of();

	public List<String> getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(List<String> allowedOrigins) {
		// Trim para evitar errores por espacios accidentales
		this.allowedOrigins = allowedOrigins.stream().map(String::trim).toList();
	}

}
