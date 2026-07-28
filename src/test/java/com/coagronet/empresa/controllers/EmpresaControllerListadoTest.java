/*=============================================================================
 Nombre del archivo : EmpresaControllerListadoTest.java
 Descripcion        : Pruebas web y de seguridad del listado de empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion de pruebas del endpoint, filtros y acceso de la HU-043.2.                                                                |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.empresa.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.coagronet.empresa.dtos.EmpresaListadoFiltroDTO;
import com.coagronet.empresa.dtos.EmpresaListadoItemDTO;
import com.coagronet.empresa.dtos.EmpresaListadoResponseDTO;
import com.coagronet.empresa.services.EmpresaService;
import com.coagronet.exceptionHandler.Advice;
import com.coagronet.exceptionHandler.custom.CustomAccessDeniedHandler;
import com.coagronet.exceptionHandler.custom.CustomAuthenticationEntryPoint;
import com.coagronet.infrastructure.configuration.CorsProperties;
import com.coagronet.infrastructure.configuration.SecurityConfig;
import com.coagronet.infrastructure.security.JwtAuthenticationFilter;
import com.coagronet.infrastructure.security.MyUserDetailsService;
import com.coagronet.menu.services.MenuService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@WebMvcTest(controllers = EmpresaController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
				classes = JwtAuthenticationFilter.class))
@Import({ SecurityConfig.class, Advice.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class })
class EmpresaControllerListadoTest {

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
	void listar_retorna401SinAutenticacion() throws Exception {
		mockMvc.perform(get("/api/v1/empresas"))
			.andExpect(status().isUnauthorized());

		verifyNoInteractions(empresaService);
	}

	@Test
	@WithMockUser(roles = "GERENTE")
	void listar_retorna403SinPermiso() throws Exception {
		mockMvc.perform(get("/api/v1/empresas"))
			.andExpect(status().isForbidden());

		verifyNoInteractions(empresaService);
	}

	@Test
	@WithMockUser(authorities = "EMPRESA_READ_ALL")
	void listar_retorna200ConPermisoYAplicaFiltros() throws Exception {
		when(empresaService.listar(any(EmpresaListadoFiltroDTO.class), any(Pageable.class)))
			.thenReturn(responseValido());

		mockMvc.perform(get("/api/v1/empresas")
			.param("tipoIdentificacionId", "5")
			.param("identificacion", "900")
			.param("nombre", "Demo")
			.param("correo", "correo@demo.com")
			.param("estadoId", "1")
			.param("page", "0")
			.param("size", "20")
			.param("sortBy", "nombre,desc"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.header.totalElements").value(1))
			.andExpect(jsonPath("$.data[0].nombre").value("Empresa Demo"))
			.andExpect(jsonPath("$.data[0].estadoNombre").value("Activo"));

		ArgumentCaptor<EmpresaListadoFiltroDTO> filtroCaptor = ArgumentCaptor.forClass(EmpresaListadoFiltroDTO.class);
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(empresaService).listar(filtroCaptor.capture(), pageableCaptor.capture());

		EmpresaListadoFiltroDTO filtro = filtroCaptor.getValue();
		assertEquals(5L, filtro.getTipoIdentificacionId());
		assertEquals("900", filtro.getIdentificacion());
		assertEquals("Demo", filtro.getNombre());
		assertEquals("correo@demo.com", filtro.getCorreo());
		assertEquals(1L, filtro.getEstadoId());
		assertEquals(20, pageableCaptor.getValue().getPageSize());
		assertTrue(pageableCaptor.getValue().getSort().getOrderFor("nombre").isDescending());
	}

	@Test
	@WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
	void listar_retorna200ParaAdministradorSistema() throws Exception {
		when(empresaService.listar(any(EmpresaListadoFiltroDTO.class), any(Pageable.class)))
			.thenReturn(responseValido());

		mockMvc.perform(get("/api/v1/empresas"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities = "EMPRESA_READ_ALL")
	void listar_retorna400ConOrdenamientoInvalido() throws Exception {
		mockMvc.perform(get("/api/v1/empresas").param("sortBy", "campoInexistente,asc"))
			.andExpect(status().isBadRequest());

		verifyNoInteractions(empresaService);
	}

	@Test
	@WithMockUser(authorities = "EMPRESA_READ_ALL")
	void listar_retorna400CuandoPageEsNegativo() throws Exception {
		mockMvc.perform(get("/api/v1/empresas").param("page", "-1"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.title").value("Error de Validación"))
			.andExpect(jsonPath("$.errors.page").isString());

		verifyNoInteractions(empresaService);
	}

	@Test
	@WithMockUser(authorities = "EMPRESA_READ_ALL")
	void listar_retorna400CuandoSizeSuperaElMaximo() throws Exception {
		mockMvc.perform(get("/api/v1/empresas").param("size", "101"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.title").value("Error de Validación"))
			.andExpect(jsonPath("$.errors.size").isString());

		verifyNoInteractions(empresaService);
	}

	private EmpresaListadoResponseDTO responseValido() {
		EmpresaListadoItemDTO item = EmpresaListadoItemDTO.builder()
			.id(1L)
			.tipoIdentificacionId(5L)
			.tipoIdentificacionNombre("NIT")
			.identificacion("900123")
			.nombre("Empresa Demo")
			.correo("correo@demo.com")
			.estadoId(1L)
			.estadoNombre("Activo")
			.build();
		return EmpresaListadoResponseDTO.builder()
			.header(EmpresaListadoResponseDTO.Paginacion.builder()
				.totalElements(1)
				.totalPages(1)
				.size(10)
				.number(0)
				.first(true)
				.last(true)
				.numberOfElements(1)
				.empty(false)
				.build())
			.data(List.of(item))
			.build();
	}

}
