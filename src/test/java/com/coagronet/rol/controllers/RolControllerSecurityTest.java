/*=============================================================================
 Nombre del archivo : RolControllerSecurityTest.java
 Descripcion        : Pruebas unitarias y de seguridad para el controlador
                      de roles.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-22 | 0.4.0   | JUAN JOSE CASTRO     | Reemplazo del uso de        |
 |            |         |                      | OffsetDateTime por Instant  |
 |            |         |                      | en la inicialización de los |
 |            |         |                      | mocks de RolResponseDTO.    |
 |            |         |                      | Reorganización de imports y |
 |            |         |                      | ajustes menores de formato. |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.rol.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.coagronet.exceptionHandler.Advice;
import com.coagronet.exceptionHandler.custom.CustomAccessDeniedHandler;
import com.coagronet.exceptionHandler.custom.CustomAuthenticationEntryPoint;
import com.coagronet.infrastructure.configuration.CorsProperties;
import com.coagronet.infrastructure.configuration.SecurityConfig;
import com.coagronet.infrastructure.security.JwtAuthenticationFilter;
import com.coagronet.infrastructure.security.MyUserDetailsService;
import com.coagronet.rol.dtos.RolResponseDTO;
import com.coagronet.rol.services.impl.RolServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@WebMvcTest(controllers = RolController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        JwtAuthenticationFilter.class }))
@Import({ SecurityConfig.class, Advice.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class })
class RolControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RolServiceImpl rolService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @MockBean
    private CorsProperties corsProperties;

    @BeforeEach
    void setup() throws Exception {
        when(corsProperties.getAllowedOrigins()).thenReturn(List.of());
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            try {
                chain.doFilter(invocation.getArgument(0, ServletRequest.class),
                        invocation.getArgument(1, ServletResponse.class));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(ServletRequest.class), any(ServletResponse.class),
                any(FilterChain.class));
    }

    @Test
    void getAll_returns401_whenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(rolService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAll_returns403_whenUserLacksAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(rolService);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void getAll_returns403_whenUserIsAdministradorEmpresa_currentSecurityRule() throws Exception {
        when(rolService.getAll()).thenReturn(List.of(
                new RolResponseDTO(1L, "Operario", "Rol operativo", 1L, "Activo", "admin", Instant.now(),
                        null, null)));

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
    void create_returns201AndLocation_whenRequestIsValid() throws Exception {
        when(rolService.create(any())).thenReturn(
                new RolResponseDTO(10L, "Operario", "Rol operativo", 1L, "Activo", "admin", Instant.now(),
                        null, null));

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRequestJson("Operario")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/roles/10"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
    void create_returns400_whenNombreIsMissing() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("descripcion", "Rol operativo");
        json.put("estadoId", 1L);

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(rolService);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
    void update_returns204_whenRequestIsValid() throws Exception {
        mockMvc.perform(put("/api/v1/roles/{id}", 10L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRequestJson("Operario actualizado")))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
    void softDelete_returns204_whenRequestIsValid() throws Exception {
        mockMvc.perform(delete("/api/v1/roles/{id}", 10L))
                .andExpect(status().isNoContent());
    }

    private String buildRequestJson(String nombre) throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("nombre", nombre);
        json.put("descripcion", "Rol operativo");
        json.put("estadoId", 1L);
        return objectMapper.writeValueAsString(json);
    }
}
