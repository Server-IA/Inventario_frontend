package com.coagronet.rolpermiso.controllers;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import com.coagronet.rolpermiso.dtos.response.ModuloPermisoResponse;
import com.coagronet.rolpermiso.dtos.response.RolPermisoAsignadoResponse;
import com.coagronet.rolpermiso.services.RolPermisoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@WebMvcTest(controllers = RolPermisoController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        JwtRequestFilter.class, JwtAuthenticationFilter.class }))
@Import({ SecurityConfig.class, Advice.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class })
class RolPermisoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RolPermisoService rolPermisoService;

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
    void getModulosDisponibles_returns401_whenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/empresa-rol-permisos/modulos-disponibles"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(rolPermisoService);
    }

    @Test
    @WithMockUser(roles = "USER")
        void getModulosDisponibles_returns200_whenUserIsAuthenticated_currentBehavior() throws Exception {
        Page<ModuloPermisoResponse> page = new PageImpl<>(List.of(
            ModuloPermisoResponse.builder().moduloId(1L).moduloNombre("Inventario").permisos(List.of()).build()));
        when(rolPermisoService.getModulosDisponibles(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/empresa-rol-permisos/modulos-disponibles"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void getModulosDisponibles_returns200_whenUserIsAdminEmpresa() throws Exception {
        Page<ModuloPermisoResponse> page = new PageImpl<>(List.of(
            ModuloPermisoResponse.builder().moduloId(1L).moduloNombre("Inventario").permisos(List.of()).build()));
        when(rolPermisoService.getModulosDisponibles(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/empresa-rol-permisos/modulos-disponibles"))
                .andExpect(status().isOk());
    }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
        void getModulosDisponibles_returns200_whenUserIsAdminSistema() throws Exception {
            Page<ModuloPermisoResponse> page = new PageImpl<>(List.of(
                ModuloPermisoResponse.builder().moduloId(1L).moduloNombre("Inventario").permisos(List.of()).build()));
            when(rolPermisoService.getModulosDisponibles(any())).thenReturn(page);

            mockMvc.perform(get("/api/v1/empresa-rol-permisos/modulos-disponibles"))
            .andExpect(status().isOk());
        }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void asignarModulosPermisos_returns201_whenPayloadIsValid() throws Exception {
        when(rolPermisoService.asignarModulosPermisos(any(), any())).thenReturn(
                RolPermisoAsignadoResponse.builder().rolId(5L).rolNombre("Operario").permisosAsignados(2).build());

        mockMvc.perform(post("/api/v1/empresa-rol-permisos/{rolId}/asignar-modulos-permisos", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildModulosPayload()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void asignarModulosPermisos_returns400_whenModulesListIsEmpty() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.putArray("modulosIds");

        mockMvc.perform(post("/api/v1/empresa-rol-permisos/{rolId}/asignar-modulos-permisos", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(rolPermisoService);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void asignarModulosPermisos_returns400_whenModulesListHasInvalidType() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.putArray("modulosIds").add("abc");

        mockMvc.perform(post("/api/v1/empresa-rol-permisos/{rolId}/asignar-modulos-permisos", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(rolPermisoService);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
    void quitarModulosPermisos_returns204_whenPayloadIsValid() throws Exception {
        mockMvc.perform(delete("/api/v1/empresa-rol-permisos/{rolId}/quitar-modulos-permisos", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildModulosPayload()))
                .andExpect(status().isNoContent());
    }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
        void getModulosBySubsistemas_returns200_whenQueryParamIsPresent() throws Exception {
        when(rolPermisoService.getModulosBySubsistemas(List.of(1L, 2L))).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/empresa-rol-permisos/modulos-subsitema")
            .param("subsistemaIds", "1", "2"))
            .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
        void getModulosBySubsistemas_returns400_whenQueryParamIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/empresa-rol-permisos/modulos-subsitema"))
            .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
        void asignarModulosPermisosLectura_returns201_whenPayloadIsValid() throws Exception {
        when(rolPermisoService.asignarModulosPermisosLectura(any(), any())).thenReturn(
            RolPermisoAsignadoResponse.builder().rolId(5L).rolNombre("Operario").permisosAsignados(2).build());

        mockMvc.perform(post("/api/v1/empresa-rol-permisos/{rolId}/asignar-modulos-lectura", 5L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(buildModulosPayload()))
            .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
        void asignarModulosPermisosConMetodos_returns201_whenPayloadIsValid() throws Exception {
        when(rolPermisoService.asignarModulosPermisosConMetodos(any(), any())).thenReturn(
            RolPermisoAsignadoResponse.builder().rolId(5L).rolNombre("Operario").permisosAsignados(1).build());

        mockMvc.perform(post("/api/v1/empresa-rol-permisos/{rolId}/asignar-modulos-metodos", 5L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(buildModulosMetodosPayload()))
            .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
        void reemplazarPermiso_returns200_whenPayloadIsValid() throws Exception {
        when(rolPermisoService.reemplazarPermisoDeEmpresaRol(any(), any(), any())).thenReturn(
            RolPermisoAsignadoResponse.builder().rolId(5L).rolNombre("Operario").permisosAsignados(1).build());

        mockMvc.perform(put("/api/v1/empresa-rol-permisos/{rolId}/reemplazar-permiso", 5L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(buildReemplazarPermisoPayload()))
            .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
        void reemplazarModulo_returns200_whenPayloadIsValid() throws Exception {
        when(rolPermisoService.reemplazarModuloPermisosDeEmpresaRol(any(), any(), any())).thenReturn(
            RolPermisoAsignadoResponse.builder().rolId(5L).rolNombre("Operario").permisosAsignados(2).build());

        mockMvc.perform(put("/api/v1/empresa-rol-permisos/{rolId}/reemplazar-modulo", 5L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(buildReemplazarModuloPayload()))
            .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
        void asignarPermisosLegacy_returns201_whenPayloadIsValid() throws Exception {
        mockMvc.perform(post("/api/v1/empresa-rol-permisos/rol/{rolId}/permisos", 5L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(buildPermisosPayload()))
            .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR_EMPRESA")
        void quitarPermisosLegacy_returns204_whenPayloadIsValid() throws Exception {
        mockMvc.perform(delete("/api/v1/empresa-rol-permisos/rol/{rolId}/permisos/quitar", 5L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(buildPermisosPayload()))
            .andExpect(status().isNoContent());
        }

    private String buildModulosPayload() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.putArray("modulosIds").add(10L).add(20L);
        return objectMapper.writeValueAsString(json);
    }

    private String buildModulosMetodosPayload() throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        ObjectNode moduloMetodo = objectMapper.createObjectNode();
        moduloMetodo.put("moduloId", 10L);
        moduloMetodo.putArray("metodos").add("GET").add("POST");
        root.putArray("modulosMetodos").add(moduloMetodo);
        return objectMapper.writeValueAsString(root);
    }

    private String buildReemplazarPermisoPayload() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("permisoIdActual", 100L);
        json.put("nuevoPermisoId", 105L);
        return objectMapper.writeValueAsString(json);
    }

    private String buildReemplazarModuloPayload() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("moduloIdActual", 342L);
        json.put("nuevoModuloId", 340L);
        return objectMapper.writeValueAsString(json);
    }

    private String buildPermisosPayload() throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.putArray("permisosId").add(1L).add(2L);
        return objectMapper.writeValueAsString(json);
    }
}
