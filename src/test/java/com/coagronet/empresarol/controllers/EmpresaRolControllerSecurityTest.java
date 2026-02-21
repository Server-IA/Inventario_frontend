package com.coagronet.empresarol.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.coagronet.empresarol.dtos.responses.EmpresaRolResponseDTO;
import com.coagronet.empresarol.services.EmpresaRolService;
import com.coagronet.exceptionHandler.Advice;
import com.coagronet.exceptionHandler.UserRoleForbiddenException;
import com.coagronet.exceptionHandler.custom.CustomAccessDeniedHandler;
import com.coagronet.exceptionHandler.custom.CustomAuthenticationEntryPoint;
import com.coagronet.infrastructure.configuration.CorsProperties;
import com.coagronet.infrastructure.configuration.SecurityConfig;
import com.coagronet.infrastructure.security.JwtAuthenticationFilter;
import com.coagronet.infrastructure.security.JwtRequestFilter;
import com.coagronet.infrastructure.security.JwtService;
import com.coagronet.infrastructure.security.MyUserDetailsService;
import com.coagronet.utils.UriBuilderUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@WebMvcTest(controllers = EmpresaRolController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        JwtRequestFilter.class, JwtAuthenticationFilter.class }))
@Import({ SecurityConfig.class, Advice.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class })
class EmpresaRolControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmpresaRolService empresaRolService;

    @MockBean
    private UriBuilderUtil uriBuilderUtil;

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
    void findAll_returns401_whenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/empresa-rol"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(empresaRolService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAll_returns403_whenUserLacksRequiredRole() throws Exception {
        mockMvc.perform(get("/api/v1/empresa-rol"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(empresaRolService);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void findAll_returns200_whenUserIsAdminEmpresa() throws Exception {
        when(empresaRolService.findAll()).thenReturn(List.of(new EmpresaRolResponseDTO(1L, "Empresa A", "Operario", "Activo")));

        mockMvc.perform(get("/api/v1/empresa-rol"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
    void findAll_returns200_whenUserIsAdminSistema() throws Exception {
        when(empresaRolService.findAll()).thenReturn(List.of(new EmpresaRolResponseDTO(1L, "Empresa A", "Operario", "Activo")));

        mockMvc.perform(get("/api/v1/empresa-rol"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void create_returns201_whenPayloadIsValid() throws Exception {
        when(empresaRolService.create(any())).thenReturn(new EmpresaRolResponseDTO(99L, "Empresa A", "Operario", "Activo"));

        mockMvc.perform(post("/api/v1/empresa-rol")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateJson()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void create_returns400_whenRolIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/empresa-rol")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(empresaRolService);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void create_returns400_whenRolIdHasInvalidType() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("rolId", "texto");

        mockMvc.perform(post("/api/v1/empresa-rol")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(empresaRolService);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void create_returns403_whenRolIsAdministradorSistema() throws Exception {
        when(empresaRolService.create(any())).thenThrow(new UserRoleForbiddenException("No puedes asignar ese rol"));

        mockMvc.perform(post("/api/v1/empresa-rol")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateAdminSistemaJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void update_returns204_whenPayloadIsValid() throws Exception {
        mockMvc.perform(put("/api/v1/empresa-rol/{id}", 20L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildUpdateJson()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void update_returns403_whenRolIsAdministradorSistema() throws Exception {
        when(empresaRolService.update(any(), any())).thenThrow(new UserRoleForbiddenException("No puedes asignar ese rol"));

        mockMvc.perform(put("/api/v1/empresa-rol/{id}", 20L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildUpdateAdminSistemaJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void delete_returns204_whenUserHasAccess() throws Exception {
        mockMvc.perform(delete("/api/v1/empresa-rol/{id}", 20L))
                .andExpect(status().isNoContent());
    }

    private String buildCreateJson() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("rolId", 3L);
        return objectMapper.writeValueAsString(json);
    }

    private String buildUpdateJson() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("rolId", 3L);
        json.put("estadoId", 1L);
        return objectMapper.writeValueAsString(json);
    }

    private String buildCreateAdminSistemaJson() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("rolId", 1L);
        return objectMapper.writeValueAsString(json);
    }

    private String buildUpdateAdminSistemaJson() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("rolId", 1L);
        return objectMapper.writeValueAsString(json);
    }
}
