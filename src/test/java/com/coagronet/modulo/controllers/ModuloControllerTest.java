package com.coagronet.modulo.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.coagronet.infrastructure.configuration.CorsProperties;
import com.coagronet.infrastructure.security.JwtAuthenticationFilter;
import com.coagronet.infrastructure.security.JwtRequestFilter;
import com.coagronet.modulo.dtos.ModuloDetailResponse;
import com.coagronet.modulo.dtos.ModuloRequest;
import com.coagronet.modulo.dtos.ModuloSummaryResponse;
import com.coagronet.modulo.services.ModuloService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
    controllers = ModuloController.class,
    excludeAutoConfiguration = { SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class },
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthenticationFilter.class,
        JwtRequestFilter.class }))
@AutoConfigureMockMvc(addFilters = false)
class ModuloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ModuloService moduloService;

    @MockBean
    private CorsProperties corsProperties;

    @BeforeEach
    void setup() {
        when(corsProperties.getAllowedOrigins()).thenReturn(List.of());
    }

    @Test
    void crear_returns201AndLocationHeader_whenRequestIsValid() throws Exception {
        ModuloRequest request = new ModuloRequest(
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

        when(moduloService.crearModulo(request)).thenReturn(10L);

        mockMvc.perform(post("/api/v1/modulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/modulos/10"));

        verify(moduloService).crearModulo(request);
    }

    @Test
    void crear_returns400_whenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/modulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(moduloService);
    }

        @Test
        void actualizar_returns204_whenRequestIsValid() throws Exception {
        Long moduloId = 10L;
        ModuloRequest request = new ModuloRequest(
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

        mockMvc.perform(put("/api/v1/modulos/{id}", moduloId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());

        verify(moduloService).actualizarModulo(moduloId, request);
        }

        @Test
        void actualizar_returns400_whenRequestIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/modulos/{id}", 10L)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(moduloService);
        }

        @Test
        void obtenerModulos_returns200AndPage_whenServiceReturnsPage() throws Exception {
        Page<ModuloSummaryResponse> page = new PageImpl<>(List.of(
            new ModuloSummaryResponse(
                1L,
                "Inventario",
                "/inventario",
                "Modulo de inventario",
                "fa-box",
                "Activo",
                "Seguridad",
                "CRUD",
                "Web",
                new String[] { "ADMIN" },
                "mod_inventario",
                true)),
            PageRequest.of(0, 10),
            1);

        when(moduloService.obtenerModulos(org.mockito.ArgumentMatchers.any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/modulos")
            .queryParam("page", "0")
            .queryParam("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1));
        }

        @Test
        void obtenerDetalleModulo_returns200_whenModuloExists() throws Exception {
        Long moduloId = 5L;
        ModuloDetailResponse response = new ModuloDetailResponse(
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
            true);

        when(moduloService.obtenerDetalleModulo(moduloId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/modulos/{id}", moduloId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Inventario"))
            .andExpect(jsonPath("$.requerido").value(true));

        verify(moduloService).obtenerDetalleModulo(moduloId);
        }
}

