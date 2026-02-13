package com.coagronet.modulo.controllers;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.coagronet.infrastructure.configuration.CorsProperties;
import com.coagronet.infrastructure.configuration.SecurityConfig;
import com.coagronet.infrastructure.security.JwtAuthenticationFilter;
import com.coagronet.infrastructure.security.JwtRequestFilter;
import com.coagronet.infrastructure.security.JwtService;
import com.coagronet.infrastructure.security.MyUserDetailsService;
import com.coagronet.modulo.dtos.ModuloDetailResponse;
import com.coagronet.modulo.dtos.ModuloRequest;
import com.coagronet.modulo.dtos.ModuloSummaryResponse;
import com.coagronet.modulo.services.ModuloService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ModuloController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
    JwtRequestFilter.class, JwtAuthenticationFilter.class }))
@Import(SecurityConfig.class)
class ModuloControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ModuloService moduloService;

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
    void crear_returns401_whenUserIsUnauthenticated() throws Exception {
        ModuloRequest request = buildRequest();

        mockMvc.perform(post("/api/v1/modulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(moduloService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void crear_returns403_whenUserLacksAdminRole() throws Exception {
        ModuloRequest request = buildRequest();

        mockMvc.perform(post("/api/v1/modulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(moduloService);
    }

        @Test
        void obtenerModulos_returns401_whenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/modulos"))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(moduloService);
        }

        @Test
        @WithMockUser(roles = "USER")
        void obtenerModulos_returns403_whenUserLacksAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/modulos"))
            .andExpect(status().isForbidden());

        verifyNoInteractions(moduloService);
        }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
        void obtenerModulos_returns200_whenUserIsAdmin() throws Exception {
        Page<ModuloSummaryResponse> page = new PageImpl<>(List.of(
            new ModuloSummaryResponse(1L, "Inventario", "/inventario", "Modulo de inventario", "fa-box",
                "Activo", "Seguridad", "CRUD", "Web", new String[] { "ADMIN" }, "mod_inventario", true)),
            PageRequest.of(0, 10), 1);
        when(moduloService.obtenerModulos(org.mockito.ArgumentMatchers.any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/modulos"))
            .andExpect(status().isOk());
        }

        @Test
        void obtenerDetalle_returns401_whenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/modulos/{id}", 1L))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(moduloService);
        }

        @Test
        @WithMockUser(roles = "USER")
        void obtenerDetalle_returns403_whenUserLacksAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/modulos/{id}", 1L))
            .andExpect(status().isForbidden());

        verifyNoInteractions(moduloService);
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
            new String[] { "ADMIN" },
            "mod_inventario",
            true));

        mockMvc.perform(get("/api/v1/modulos/{id}", 1L))
            .andExpect(status().isOk());
        }

        @Test
        void actualizar_returns401_whenUserIsUnauthenticated() throws Exception {
        ModuloRequest request = buildRequest();

        mockMvc.perform(put("/api/v1/modulos/{id}", 10L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(moduloService);
        }

        @Test
        @WithMockUser(roles = "USER")
        void actualizar_returns403_whenUserLacksAdminRole() throws Exception {
        ModuloRequest request = buildRequest();

        mockMvc.perform(put("/api/v1/modulos/{id}", 10L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(moduloService);
        }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
        void actualizar_returns204_whenUserIsAdmin() throws Exception {
        ModuloRequest request = buildRequest();

        mockMvc.perform(put("/api/v1/modulos/{id}", 10L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());
        }

    private ModuloRequest buildRequest() {
        return new ModuloRequest(
                "Compras",
                "/compras",
                "Modulo de compras",
                "fa-cart",
                1L,
                2L,
                3L,
                4L,
                List.of("ADMIN"),
                "mod_compras",
                true);
    }
}

