package com.coagronet.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final CorsProperties corsProperties; // inyectamos tu clase de propiedades

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // aplica a todo
			.allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0])) // orígenes
																						// confiables
			.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
			.allowedHeaders("*") // o lista fina si prefieres
			.allowCredentials(true)
			.maxAge(3600); // 1 h cache pre‑flight
	}

}
