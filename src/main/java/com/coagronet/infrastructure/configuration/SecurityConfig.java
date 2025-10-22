package com.coagronet.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import com.coagronet.infrastructure.security.JwtRequestFilter;
import com.coagronet.infrastructure.security.JwtService;
import com.coagronet.infrastructure.security.MyUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final MyUserDetailsService myUserDetailsService;

	private final JwtService jwtService;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth
				.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/auth/**",
						"/api/v1/empresas/**", "/api/v1/personas/**", "/api/v1/menu/**")
				.permitAll()
				.requestMatchers("/api/v1/tipo_identificacion/**", "/api/v1/estado/**", "/api/v1/persona/**",
						"/api/v1/movimiento/**", "/api/v1/tipo-evaluacion/**", "/api/v1/user/**",
						"/api/v1/categoria-estado/**")
				.hasAnyRole("ADMINISTRADOR_SISTEMA")
				.requestMatchers("/api/v1/pais/**", "/api/v1/departamento/**", "/api/v1/municipio/**",
						"/api/v1/marca/**", "/api/v1/tipo_bloque/**", "/api/v1/tipo_espacio/**", "/api/v1/tipo_sede/**",
						"/api/v1/grupo/**", "/api/v1/sede/**", "/api/v1/bloque/**", "/api/v1/espacio/**",
						"/api/v1/almacen/**", "/api/v1/producto_categoria/**", "/api/v1/tipo_produccion/**",
						"/api/v1/proceso/**", "/api/v1/tipo_movimiento/**", "/api/v1/ingrediente/**",
						"/api/v1/articulo-pedido/**", "/api/v1/articulo-orden-compra/**", "/api/v1/articulo-kardex/**",
						"/api/v1/ocupacion/**", "/api/v1/control_inventario/**", "/api/v1/tipo_inventario/**",
						"/api/v1/inventario/**", "/api/v1/seccion/**", "/api/v1/orden-compra/**",
						"/api/v1/criterio_evaluacion/**", "/api/v1/evaluacion/**",
						"/api/v1/ingrediente-presentacion-producto/**", "/api/v1/proveedor/**", "/api/v1/items/**",
						"/api/v1/articulo-inventario/**", "/api/v1/kardex/**", "/api/v1/espacio_ocupacion/**",
						"/api/v1/inventario_item/**", "/api/v1/pedido/**", "/api/v1/presentacion/**",
						"/api/v1/producto_presentacion/**", "/api/v1/produccion/**", "/api/v1/producto/**",
						"/api/v1/producto_localizacion/**", "/api/v1/subseccion/**", "/api/report/**",
						"/api/v1/factura/**", "/api/v1/pedido-cotizacion/**", "/api/v2/menu")
				.hasAnyRole("ADMINISTRADOR_SISTEMA", "ADMINISTRADOR_EMPRESA").requestMatchers("/api/v2/report/**")
				.hasAnyRole("ADMINISTRADOR_SISTEMA", "ADMINISTRADOR_EMPRESA", "GERENTE").anyRequest().authenticated())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
						"/api/v1/factura/**", "/api/v1/pedido-cotizacion/**", "/api/v1/metricas/**")
				.hasAnyRole("ADMINISTRADOR_SISTEMA", "ADMINISTRADOR_EMPRESA")
				.requestMatchers("/api/v2/report/**")
				.hasAnyRole("ADMINISTRADOR_SISTEMA", "ADMINISTRADOR_EMPRESA", "GERENTE")
				.anyRequest()
				.authenticated())
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class); // <<<<<<

		return http.build();
	}

	@Bean
	JwtRequestFilter jwtRequestFilter() {
		return new JwtRequestFilter(jwtService, myUserDetailsService);
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
