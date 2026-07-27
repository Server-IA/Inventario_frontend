/*=============================================================================
 Nombre del archivo : SecurityConfig.java
 Descripcion        : Configuracion de seguridad y reglas de autorizacion de la API.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2024-08-16 | 1.0.0   | yourusername         | Creacion del archivo.                                                                                                              |
 | 2026-05-27 | 1.1.0   | JUAN DIAZ            | Refactor de catalogos globales: ajustes en entidades, DTOs, mappers, repositorios y servicios, con validaciones de negocio.        |
 | 2026-07-27 | 1.2.0   | JUAN DIAZ            | Se protege el registro de empresas con la autoridad EMPRESA_CREATE para la HU-043.1.                                               |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.coagronet.exceptionHandler.custom.CustomAccessDeniedHandler;
import com.coagronet.exceptionHandler.custom.CustomAuthenticationEntryPoint;
import com.coagronet.infrastructure.security.JwtAuthenticationFilter;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAccessDeniedHandler accessDeniedHandler,
			CustomAuthenticationEntryPoint authenticationEntryPoint) throws Exception {
		http.cors(Customizer.withDefaults())
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/empresas")
						.hasAnyAuthority("EMPRESA_CREATE", "ROLE_ADMINISTRADOR_SISTEMA")
						.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/auth/**",
								"/api/v1/empresas/**", "/api/v1/personas/**", "/api/v2/menu/**",
								"/change-password-initial/**")
						.permitAll()
						.requestMatchers("/api/v1/tipo_identificacion/**", "/api/v1/estado/**", "/api/v1/persona/**",
								"/api/v1/movimiento/**", "/api/v1/tipo-evaluacion/**", "/api/v1/user/**",
								"/api/v1/categoria-estado/**", "/api/v1/roles/**", "/api/v1/system/usuario-roles/**",
								"/api/v1/system/empresa-rol/**", "/api/v2/modulos/**", "/api/v1/tipo-modulos/**",
								"/api/v1/tipo-aplicaciones/**")
						.hasAuthority("ROLE_ADMINISTRADOR_SISTEMA")
						// Especifidad para unidad y tipoUnidad
						.requestMatchers(HttpMethod.GET, "/api/v1/tipo-unidad/**")
						.authenticated()
						.requestMatchers(HttpMethod.GET, "/api/v1/unidad/**")
						.authenticated()
						.requestMatchers(HttpMethod.POST, "/api/v1/unidad/**")
						.hasRole("ADMINISTRADOR_SISTEMA")
						.requestMatchers(HttpMethod.PUT, "/api/v1/unidad/**")
						.hasRole("ADMINISTRADOR_SISTEMA")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/unidad/**")
						.hasRole("ADMINISTRADOR_SISTEMA")
						.requestMatchers(HttpMethod.POST, "/api/v1/tipo-unidad/**")
						.hasRole("ADMINISTRADOR_SISTEMA")
						.requestMatchers(HttpMethod.PUT, "/api/v1/tipo-unidad/**")
						.hasRole("ADMINISTRADOR_SISTEMA")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/tipo-unidad/**")
						.hasRole("ADMINISTRADOR_SISTEMA")
						.requestMatchers("/api/v1/pais/**", "/api/v1/departamento/**", "/api/v1/municipio/**")
						.hasRole("ADMINISTRADOR_SISTEMA")
						.requestMatchers("/api/v1/marca/**", "/api/v1/tipo_bloque/**", "/api/v1/tipo_espacio/**",
								"/api/v1/tipo_sede/**",
								"/api/v1/grupo/**", "/api/v1/sede/**", "/api/v1/bloque/**", "/api/v1/espacio/**",
								"/api/v1/almacen/**", "/api/v1/producto_categoria/**", "/api/v1/tipo_produccion/**",
								"/api/v1/proceso/**", "/api/v1/tipo_movimiento/**", "/api/v1/ingrediente/**",
								"/api/v1/articulo-pedido/**", "/api/v1/articulo-orden-compra/**",
								"/api/v1/articulo-kardex/**",
								"/api/v1/ocupacion/**", "/api/v1/control_inventario/**", "/api/v1/tipo_inventario/**",
								"/api/v1/inventario/**", "/api/v1/seccion/**", "/api/v1/orden-compra/**",
								"/api/v1/criterio_evaluacion/**", "/api/v1/evaluacion/**",
								"/api/v1/ingrediente-presentacion-producto/**", "/api/v1/proveedor/**",
								"/api/v1/items/**",
								"/api/v1/articulo-inventario/**", "/api/v1/kardex/**", "/api/v1/espacio_ocupacion/**",
								"/api/v1/inventario_item/**", "/api/v1/pedido/**", "/api/v1/presentacion/**",
								"/api/v1/producto_presentacion/**", "/api/v1/produccion/**", "/api/v2/producto/**",
								"/api/v1/producto_localizacion/**", "/api/v1/subseccion/**", "/api/report/**",
								"/api/v1/factura/**", "/api/v1/pedido-cotizacion/**", "/api/v2/menu",
								"/api/v1/metricas/**",
								"/api/v1/estado_categoria/**", "/api/v1/empresa-rol/**", "/api/v1/usuario-roles/**",
								"/empresa/usuarios-roles/**")
						.hasAnyRole("ADMINISTRADOR_SISTEMA", "ADMINISTRADOR_EMPRESA")
						.requestMatchers("/api/v2/report/**")
						.hasAnyRole("ADMINISTRADOR_SISTEMA", "ADMINISTRADOR_EMPRESA", "GERENTE")
						.anyRequest()
						.authenticated())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exceptions -> exceptions.accessDeniedHandler(accessDeniedHandler)
						.authenticationEntryPoint(authenticationEntryPoint));

		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}









