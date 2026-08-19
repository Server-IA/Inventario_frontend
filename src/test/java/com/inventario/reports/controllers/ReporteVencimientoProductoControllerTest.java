/*=============================================================================
 Nombre del archivo : ReporteVencimientoProductoControllerTest.java
 Descripcion        : Pruebas del contrato HTTP del reporte de vencimiento de producto.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-18 | 1.0.0   | JUAN DIAZ            | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.reports.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.inventario.infrastructure.configuration.CorsProperties;
import com.inventario.infrastructure.security.JwtAuthenticationFilter;
import com.inventario.infrastructure.security.MyUserDetailsService;
import com.inventario.reports.dtos.ReporteVencimientoProductoFiltroDTO;
import com.inventario.reports.exceptions.ReporteVencimientoProductoException;
import com.inventario.reports.services.ReporteVencimientoProductoService;
import com.inventario.reports.services.ReporteVencimientoProductoService.ReporteVencimientoProductoFormato;

@WebMvcTest(
        controllers = ReporteVencimientoProductoController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ReporteVencimientoProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteVencimientoProductoService service;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @MockBean
    private CorsProperties corsProperties;

    @BeforeEach
    void setup() {
        when(corsProperties.getAllowedOrigins()).thenReturn(List.of());
    }

    @Test
    void exportar_returnsLocalizedNotFound_whenThereAreNoResults() throws Exception {
        when(service.exportar(
                any(ReporteVencimientoProductoFiltroDTO.class),
                eq(ReporteVencimientoProductoFormato.PDF),
                any(Locale.class)))
                .thenThrow(new ReporteVencimientoProductoException(
                        "report.vencimiento.no-results.export",
                        HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/api/v2/report/vencimiento-producto/exportar")
                .queryParam("formato", "PDF")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "es")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "sedeId": 86,
                          "fechaInicio": "2025-11-27",
                          "fechaFin": "2025-12-25",
                          "estado": "VENCIDO"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.detail")
                        .value("No hay productos que cumplan los criterios de consulta; no se genero el reporte."));
    }
}
