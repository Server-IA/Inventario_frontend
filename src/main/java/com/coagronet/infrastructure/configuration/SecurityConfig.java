package com.coagronet.infrastructure.configuration;

import java.util.List;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(Customizer.withDefaults())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/v3/api-docs/**",
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/**",
                                                                "/auth/**")
                                                .permitAll()
                                                .requestMatchers("/api/v1/tipo_identificacion/**",
                                                                "/api/v1/estado/**",
                                                                "/api/v1/persona/**",
                                                                "/api/v1/movimiento/**",
                                                                "/api/v1/tipo-evaluacion/**")
                                                .hasAnyRole("ADMINISTRADOR_SISTEMA")
                                                .requestMatchers("/api/v1/pais/**",
                                                                "/api/v1/departamento/**",
                                                                "/api/v1/municipio/**",
                                                                "/api/v1/marca/**",
                                                                "/api/v1/tipo_bloque/**",
                                                                "/api/v1/tipo_espacio/**",
                                                                "/api/v1/tipo_sede/**",
                                                                "/api/v1/grupo/**",
                                                                "/api/v1/sede/**",
                                                                "/api/v1/bloque/**",
                                                                "/api/v1/espacio/**",
                                                                "/api/v1/almacen/**",
                                                                "/api/v1/producto_categoria/**",
                                                                "/api/v1/tipo_produccion/**",
                                                                "/api/v1/proceso/**",
                                                                "/api/v1/movimiento/**",
                                                                "/api/v1/ingrediente/**",
                                                                "/api/v1/articulo-pedido/**",
                                                                "/api/v1/articulo-orden-compra/**",
                                                                "/api/v1/articulo-kardex/**",
                                                                "/api/v1/ocupacion/**",
                                                                "/api/v1/control_inventario/**",
                                                                "/api/v1/tipo_inventario/**",
                                                                "/api/v1/inventario/**",
                                                                "/api/v1/ocupacion/**",
                                                                "/api/v1/seccion/**",
                                                                "/api/v1/orden_compra/**",
                                                                "/api/v1/criterio_evaluacion/**",
                                                                "/api/v1/evaluacion/**",
                                                                "/api/v1/ingrediente-presentacion-producto/**",
                                                                "/api/v1/proveedor/**",
                                                                "/api/v1/items/**",
                                                                "/api/v1/articulo-inventario/**")
                                                .hasAnyRole("ADMINISTRADOR_SISTEMA", "ADMINISTRADOR_EMPRESA")
                                                .requestMatchers("/api/v2/report/**")
                                                .hasAnyRole("ADMINISTRADOR_SISTEMA", "ADMINISTRADOR_EMPRESA", "GERENTE")
                                                .anyRequest()
                                                .authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of(
                                "http://localhost:5173, http://inmero.co, http://www.inmero.co, https://inmero.co, https://www.inmero.co, "));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true); // ✅ Solo si estás usando tokens/cookies

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
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
