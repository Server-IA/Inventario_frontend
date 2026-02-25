package com.coagronet.rol.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
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
import com.coagronet.infrastructure.security.JwtRequestFilter;
import com.coagronet.infrastructure.security.JwtService;
import com.coagronet.infrastructure.security.MyUserDetailsService;
import com.coagronet.rol.dtos.RolResponseDTO;
import com.coagronet.rol.services.impl.RolServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@WebMvcTest(controllers = RolController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        JwtRequestFilter.class, JwtAuthenticationFilter.class }))
@Import({ SecurityConfig.class, Advice.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class })
class RolControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
        private RolServiceImpl rolService;

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
    @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
    void getAll_returns200_whenUserIsAdministradorSistema() throws Exception {
        when(rolService.getAll()).thenReturn(List.of(
                new RolResponseDTO(1L, "Operario", "Rol operativo", 1L, "Activo", "admin", OffsetDateTime.now(),
                        null, null)));

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void getAll_returns403_whenUserIsAdministradorEmpresa_rolesAreGlobal() throws Exception {
        when(rolService.getAll()).thenReturn(List.of(
                new RolResponseDTO(1L, "Operario", "Rol operativo", 1L, "Activo", "admin", OffsetDateTime.now(),
                        null, null)));

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
    void create_returns201AndLocation_whenRequestIsValid() throws Exception {
        when(rolService.create(any())).thenReturn(
                new RolResponseDTO(10L, "Operario", "Rol operativo", 1L, "Activo", "admin", OffsetDateTime.now(),
                        null, null));

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRequestJson("Operario")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/roles/10"));
    }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
        void create_returns403_whenUserIsAdministradorEmpresa_rolesAreGlobal() throws Exception {
                mockMvc.perform(post("/api/v1/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(buildRequestJson("Operario")))
                                .andExpect(status().isForbidden());

                verifyNoInteractions(rolService);
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
        void create_returns400_whenEstadoIdHasInvalidType() throws Exception {
                ObjectNode json = objectMapper.createObjectNode();
                json.put("nombre", "Operario");
                json.put("descripcion", "Rol operativo");
                json.put("estadoId", "no-numero");

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
