package com.inventario.exceptionHandler.custom;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Implementación personalizada del punto de entrada de autenticación que delega el manejo de errores.
 * <p>
 * Esta clase actúa como un puente entre la cadena de filtros de seguridad y el mecanismo de resolución de excepciones
 * global de Spring (@ControllerAdvice). Permite que las excepciones de autenticación ({@link AuthenticationException})
 * ocurridas en los filtros sean manejadas centralizadamente por un método {@code @ExceptionHandler}.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see AuthenticationEntryPoint
 * @see HandlerExceptionResolver
 * @since 2026
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    /**
     * Inicializa el punto de entrada inyectando el resolver de excepciones del manejador.
     *
     * @param resolver El {@link HandlerExceptionResolver} estándar de Spring, inyectado específicamente para delegar la
     * resolución de la excepción.
     */
    public CustomAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Inicia el esquema de autenticación delegando la excepción capturada al resolver configurado.
     * <p>
     * Este método se invoca cuando un usuario no autenticado intenta acceder a un recurso protegido. En lugar de
     * escribir directamente en la respuesta, transfiere el control al {@link HandlerExceptionResolver} para que la
     * excepción sea procesada y formateada adecuadamente por el controlador de errores global.
     * </p>
     *
     * @param request La solicitud HTTP que resultó en una excepción de autenticación.
     * @param response La respuesta HTTP donde se escribirá el error eventualmente.
     * @param authException La excepción que causó la invocación de este punto de entrada.
     * @throws IOException Si ocurre un error de entrada/salida durante la resolución.
     * @throws ServletException Si ocurre un error interno del servlet durante el proceso.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        resolver.resolveException(request, response, null, authException);
    }
}
