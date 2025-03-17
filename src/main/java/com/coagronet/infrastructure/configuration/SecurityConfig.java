package com.coagronet.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.coagronet.infrastructure.security.JwtRequestFilter;
import com.coagronet.infrastructure.security.JwtService;
import com.coagronet.infrastructure.security.MyUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/presentaciones/**",
                                "/auth/**",
                                "/api/v1/tipo_produccion",
                                "/api/v1/producciones/**",
                                "/api/v1/items/**",
                                "/api/v1/marcas/**",
                                "/api/v1/sede",
                                "/api/v1/kardex",
                                "/api/v1/kardexItem",
                                "/api/v1/unidad",
                                "/api/v1/estados/**",
                                "/api/v1/tipo_sede",
                                "/api/v1/empresas/**",
                                "/api/v1/grupo/**",
                                "/api/report/**",
                                "/api/v2/report",
                                "/api/v1/email/**",
                                "/api/v1/roles/**",
                                "/api/v1/persona",
                                "/api/v1/producto",
                                "/api/v1/producto-presentacion",
                                "/api/v1/activaciones/**",
                                "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/api/v1/tipo_identificacion/**",
                                "/api/v1/tipo_bloque/**",
                                "/api/v1/tipo_espacio/**",
                                "/api/v1/producto_categoria/**",
                                "/api/v1/tipo_movimiento",
                                "/api/v1/almacen",
                                "/api/v1/bloque",
                                "/api/v1/espacio",
                                "/api/v1/espacio_ocupacion",
                                "/api/v1/tipo_bloque",
                                "/api/v1/marca",
                                "/api/v1/tipo_espacio",
                                "/api/v1/user",
                                "/api/v1/pedido",
                                "/api/v1/pedido_item",
                                "/api/v1/actividad_ocupacion",
                                "/api/v1/orden_compra",
                                "/api/v1/orden_compra_item",
                                "/api/v1/tipo_evaluacion",
                                "/api/v1/criterio_evaluacion")
                        .permitAll()
                        .requestMatchers("/api").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(jwtService, myUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
