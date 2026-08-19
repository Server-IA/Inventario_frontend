package com.inventario.modulo.controllers;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import com.inventario.exceptionHandler.Advice;
import com.inventario.exceptionHandler.custom.CustomAccessDeniedHandler;
import com.inventario.exceptionHandler.custom.CustomAuthenticationEntryPoint;
import com.inventario.infrastructure.configuration.CorsProperties;
import com.inventario.infrastructure.configuration.SecurityConfig;
import com.inventario.infrastructure.security.JwtAuthenticationFilter;
import com.inventario.infrastructure.security.MyUserDetailsService;
import com.inventario.menu.services.MenuService;
import com.inventario.modulo.dtos.ModuloDetailResponse;
import com.inventario.modulo.dtos.ModuloSummaryResponse;
import com.inventario.modulo.services.ModuloService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@WebMvcTest(controllers = ModuloController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        JwtAuthenticationFilter.class }))
@Import({ SecurityConfig.class, Advice.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class })
class ModuloControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ModuloService moduloService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private MenuService menuService;

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
        }).when(jwtAuthenticationFilter).doFilter(any(ServletRequest.class), any(ServletResponse.class), any(FilterChain.class));
    }

    @Test
    void crear_returns401_whenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/v2/modulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRequestJson()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(moduloService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void crear_returns403_whenUserLacksAdminRole() throws Exception {
        mockMvc.perform(post("/api/v2/modulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRequestJson()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(moduloService);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
    void obtenerModulos_returns200_whenUserIsAdmin() throws Exception {
        Page<ModuloSummaryResponse> page = new PageImpl<>(List.of(
                new ModuloSummaryResponse(1L, "Inventario", "/inventario", "Modulo de inventario", "fa-box",
                        "Activo", "Seguridad", "CRUD", "Web", "mod_inventario", true)),
                PageRequest.of(0, 10), 1);
        when(moduloService.obtenerModulos(org.mockito.ArgumentMatchers.any())).thenReturn(page);

        mockMvc.perform(get("/api/v2/modulos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
    void obtenerDetalle_returns200_whenUserIsAdmin() throws Exception {
        when(moduloService.obtenerDetalleModulo(1L)).thenReturn(new ModuloDetailResponse(
                "Inventario",
                "/inventario",
                "Modulo de inventario",
                "fa-box",
                1L,
                2L,
                3L,
                4L,
                "mod_inventario",
                true));

        mockMvc.perform(get("/api/v2/modulos/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
    void actualizar_returns204_whenUserIsAdmin() throws Exception {
        mockMvc.perform(put("/api/v2/modulos/{id}", 10L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRequestJson()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
    void actualizarParcial_returns200_whenUserIsAdmin() throws Exception {
        String patchJson = buildPatchRequeridoJson(false);

        when(moduloService.actualizarRequerido(org.mockito.ArgumentMatchers.eq(10L),
                org.mockito.ArgumentMatchers.any()))
                        .thenReturn(new ModuloSummaryResponse(10L, "Compras", "/compras", "Modulo de compras", "fa-box",
                                "Activo", "Seguridad", "CRUD", "Web", "mod_compras", false));

        mockMvc.perform(patch("/api/v2/modulos/{id}", 10L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchJson))
                .andExpect(status().isOk());
    }

    private String buildRequestJson() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("nombre", "Compras");
        json.put("url", "/compras");
        json.put("descripcion", "Modulo de compras");
        json.put("estadoId", 1L);
        json.put("subSistemaId", 2L);
        json.put("tipoModuloId", 3L);
        json.put("tipoAplicacionId", 4L);
        json.put("nombreId", "mod_compras");
        json.put("requerido", true);
        return objectMapper.writeValueAsString(json);
    }

    private String buildPatchRequeridoJson(boolean requerido) throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("requerido", requerido);
        return objectMapper.writeValueAsString(json);
    }
}
