/*=============================================================================
 Nombre del archivo : EmpresaControllerDetalleTest.java
 Descripcion        : Pruebas web y de seguridad del detalle de empresa.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion de pruebas del endpoint y acceso al detalle de la HU-043.3.                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.empresa.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.inventario.empresa.dtos.EmpresaDetalleResponseDTO;
import com.inventario.empresa.services.EmpresaService;
import com.inventario.exceptionHandler.Advice;
import com.inventario.exceptionHandler.custom.CustomAccessDeniedHandler;
import com.inventario.exceptionHandler.custom.CustomAuthenticationEntryPoint;
import com.inventario.exceptionHandler.custom.RecursoNoEncontradoException;
import com.inventario.infrastructure.configuration.CorsProperties;
import com.inventario.infrastructure.configuration.SecurityConfig;
import com.inventario.infrastructure.security.JwtAuthenticationFilter;
import com.inventario.infrastructure.security.MyUserDetailsService;
import com.inventario.menu.services.MenuService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@WebMvcTest(controllers = EmpresaController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
				classes = JwtAuthenticationFilter.class))
@Import({ SecurityConfig.class, Advice.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class })
class EmpresaControllerDetalleTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EmpresaService empresaService;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockBean
	private MenuService menuService;

	@MockBean
	private MyUserDetailsService myUserDetailsService;

	@MockBean
	private CorsProperties corsProperties;

	@BeforeEach
	void setUp() throws Exception {
		when(corsProperties.getAllowedOrigins()).thenReturn(List.of());
		doAnswer(invocation -> {
			FilterChain chain = invocation.getArgument(2);
			chain.doFilter(invocation.getArgument(0, ServletRequest.class),
					invocation.getArgument(1, ServletResponse.class));
			return null;
		}).when(jwtAuthenticationFilter)
			.doFilter(any(ServletRequest.class), any(ServletResponse.class), any(FilterChain.class));
	}

	@Test
	void detalle_retorna401SinAutenticacion() throws Exception {
		mockMvc.perform(get("/api/v1/empresas/77"))
			.andExpect(status().isUnauthorized());

		verifyNoInteractions(empresaService);
	}

	@Test
	@WithMockUser(roles = "GERENTE")
	void detalle_retorna403SinPermiso() throws Exception {
		mockMvc.perform(get("/api/v1/empresas/77"))
			.andExpect(status().isForbidden());

		verifyNoInteractions(empresaService);
	}

	@Test
	@WithMockUser(authorities = "EMPRESA_READ")
	void detalle_retorna200ConTodosLosDatos() throws Exception {
		when(empresaService.obtenerDetalle(77L)).thenReturn(detalleValido());

		mockMvc.perform(get("/api/v1/empresas/77"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(77))
			.andExpect(jsonPath("$.tipoIdentificacionNombre").value("NIT"))
			.andExpect(jsonPath("$.identificacion").value("900123456"))
			.andExpect(jsonPath("$.nombre").value("Empresa Demo"))
			.andExpect(jsonPath("$.correo").value("empresa@demo.com"))
			.andExpect(jsonPath("$.celular").value("3001234567"))
			.andExpect(jsonPath("$.contacto").value("Contacto Demo"))
			.andExpect(jsonPath("$.descripcion").value("Descripcion completa"))
			.andExpect(jsonPath("$.logo").value("logo.png"))
			.andExpect(jsonPath("$.estadoNombre").value("Activo"))
			.andExpect(jsonPath("$.personaResponsableNombre").value("Ana Responsable"));
	}

	@Test
	@WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
	void detalle_retorna200ParaAdministradorSistema() throws Exception {
		when(empresaService.obtenerDetalle(77L)).thenReturn(detalleValido());

		mockMvc.perform(get("/api/v1/empresas/77"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "EMPRESA_READ")
	void detalle_retorna403CuandoEmpresaQuedaFueraDelAlcance() throws Exception {
		when(empresaService.obtenerDetalle(88L))
			.thenThrow(new AccessDeniedException("Empresa fuera del alcance"));

		mockMvc.perform(get("/api/v1/empresas/88"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(authorities = "EMPRESA_READ")
	void detalle_retorna404CuandoEmpresaNoExiste() throws Exception {
		when(empresaService.obtenerDetalle(999L))
			.thenThrow(new RecursoNoEncontradoException("Empresa", 999L));

		mockMvc.perform(get("/api/v1/empresas/999"))
			.andExpect(status().isNotFound());
	}

	private EmpresaDetalleResponseDTO detalleValido() {
		return EmpresaDetalleResponseDTO.builder()
			.id(77L)
			.tipoIdentificacionId(5L)
			.tipoIdentificacionNombre("NIT")
			.identificacion("900123456")
			.nombre("Empresa Demo")
			.correo("empresa@demo.com")
			.celular("3001234567")
			.contacto("Contacto Demo")
			.descripcion("Descripcion completa")
			.logo("logo.png")
			.estadoId(1L)
			.estadoNombre("Activo")
			.personaResponsableId(10L)
			.personaResponsableNombre("Ana Responsable")
			.build();
	}

}
