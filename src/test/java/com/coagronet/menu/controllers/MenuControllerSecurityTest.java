package com.coagronet.menu.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MockMvc;

import com.coagronet.exceptionHandler.Advice;
import com.coagronet.exceptionHandler.custom.CustomAccessDeniedHandler;
import com.coagronet.exceptionHandler.custom.CustomAuthenticationEntryPoint;
import com.coagronet.infrastructure.configuration.CorsProperties;
import com.coagronet.infrastructure.configuration.SecurityConfig;
import com.coagronet.infrastructure.security.JwtAuthenticationFilter;
import com.coagronet.infrastructure.security.JwtRequestFilter;
import com.coagronet.infrastructure.security.JwtService;
import com.coagronet.infrastructure.security.MyUserDetailsService;
import com.coagronet.menu.dtos.MenuModuloResponseDTO;
import com.coagronet.menu.dtos.MenuSubSistemaResponseDTO;
import com.coagronet.menu.services.MenuService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@WebMvcTest(controllers = MenuController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        JwtRequestFilter.class, JwtAuthenticationFilter.class }))
@Import({ SecurityConfig.class, Advice.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class })
class MenuControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuService menuService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @MockBean
    private CorsProperties corsProperties;

    @BeforeEach
    void setup() {
        when(corsProperties.getAllowedOrigins()).thenReturn(List.of());
    }

    @Test
    void listarSubsistemas_returns200_whenTipoAplicacionIsValid() throws Exception {
        MenuSubSistemaResponseDTO dto = MenuSubSistemaResponseDTO.builder()
                .nombre("Inventario")
                .icono("box")
                .modulos(List.of(new MenuModuloResponseDTO("kardex", "Kardex", "/kardex", "icon-kardex")))
                .build();

        when(menuService.obtenerMenuPorEmpresaTipoYRol("web")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v2/menu").param("tipoAplicacion", "web"))
                .andExpect(status().isOk());
    }

    @Test
    void listarSubsistemas_returns400_whenTipoAplicacionIsMissing() throws Exception {
        mockMvc.perform(get("/api/v2/menu"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void asignarModulos_returns200_whenPayloadIsValid() throws Exception {
        mockMvc.perform(post("/api/v2/menu/modulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildAsignarModulosPayload()))
                .andExpect(status().isOk());
    }

    @Test
    void asignarModulosLegacyPath_returns404() throws Exception {
        mockMvc.perform(post("/api/v2/menu/asignar-modulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildAsignarModulosPayload()))
                .andExpect(status().isNotFound());
    }

    @Test
    void asignarModulos_returns200_whenModulosIdsIsNull_currentControllerBehavior() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.putNull("modulosIds");

        mockMvc.perform(post("/api/v2/menu/modulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().isOk());
    }

    private String buildAsignarModulosPayload() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.putArray("modulosIds").add("kardex").add("producto");
        return objectMapper.writeValueAsString(json);
    }
}
