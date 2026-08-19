/*=============================================================================
 Nombre del archivo : EmpresaControllerRegistroTest.java
 Descripcion        : Pruebas web y de seguridad del registro de empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion de pruebas del endpoint y criterios de acceso de la HU-043.1.                                                            |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.empresa.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.inventario.empresa.dtos.EmpresaRegistroRequestDTO;
import com.inventario.empresa.dtos.EmpresaRegistroResponseDTO;
import com.inventario.empresa.services.EmpresaService;
import com.inventario.exceptionHandler.Advice;
import com.inventario.exceptionHandler.custom.CustomAccessDeniedHandler;
import com.inventario.exceptionHandler.custom.CustomAuthenticationEntryPoint;
import com.inventario.exceptionHandler.custom.RecursoDuplicadoException;
import com.inventario.infrastructure.configuration.CorsProperties;
import com.inventario.infrastructure.configuration.SecurityConfig;
import com.inventario.infrastructure.security.JwtAuthenticationFilter;
import com.inventario.infrastructure.security.MyUserDetailsService;
import com.inventario.menu.services.MenuService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@WebMvcTest(controllers = EmpresaController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
				classes = JwtAuthenticationFilter.class))
@Import({ SecurityConfig.class, Advice.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class })
class EmpresaControllerRegistroTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

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
	void registrar_retorna401SinAutenticacion() throws Exception {
		mockMvc.perform(post("/api/v1/empresas")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestValido())))
			.andExpect(status().isUnauthorized());

		verifyNoInteractions(empresaService);
	}

	@Test
	@WithMockUser(roles = "GERENTE")
	void registrar_retorna403SinPermiso() throws Exception {
		mockMvc.perform(post("/api/v1/empresas")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestValido())))
			.andExpect(status().isForbidden());

		verifyNoInteractions(empresaService);
	}

	@Test
	@WithMockUser(authorities = "EMPRESA_CREATE")
	void registrar_retorna201ConPermisoEmpresaCreate() throws Exception {
		when(empresaService.registrar(any(EmpresaRegistroRequestDTO.class), isNull())).thenReturn(responseValido());

		mockMvc.perform(post("/api/v1/empresas")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestValido())))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", "http://localhost/api/v1/empresas/99"))
			.andExpect(jsonPath("$.id").value(99))
			.andExpect(jsonPath("$.logoCargado").value(false));
	}

	@Test
	@WithMockUser(roles = "ADMINISTRADOR_SISTEMA")
	void registrar_retorna400CuandoFaltanCamposObligatorios() throws Exception {
		mockMvc.perform(post("/api/v1/empresas")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors.tipoIdentificacionId").exists())
			.andExpect(jsonPath("$.errors.identificacion").exists())
			.andExpect(jsonPath("$.errors.nombre").exists())
			.andExpect(jsonPath("$.errors.correo").exists())
			.andExpect(jsonPath("$.errors.personaId").exists());

		verifyNoInteractions(empresaService);
	}

	@Test
	@WithMockUser(authorities = "EMPRESA_CREATE")
	void registrar_retorna409CuandoLaIdentificacionEstaDuplicada() throws Exception {
		when(empresaService.registrar(any(EmpresaRegistroRequestDTO.class), isNull()))
			.thenThrow(new RecursoDuplicadoException("La identificacion ya existe"));

		mockMvc.perform(post("/api/v1/empresas")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestValido())))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.detail").value("La identificacion ya existe"));
	}

	@Test
	@WithMockUser(authorities = "EMPRESA_CREATE")
	void registrarMultipart_retorna201YAdvertenciaDeLogo() throws Exception {
		EmpresaRegistroResponseDTO response = responseValido();
		response.setAdvertenciaLogo("El logo fue rechazado");
		MockMultipartFile empresa = new MockMultipartFile("empresa", "", MediaType.APPLICATION_JSON_VALUE,
				objectMapper.writeValueAsBytes(requestValido()));
		MockMultipartFile logo = new MockMultipartFile("logo", "logo.jpg", MediaType.IMAGE_JPEG_VALUE,
				"logo".getBytes(StandardCharsets.UTF_8));
		when(empresaService.registrar(any(EmpresaRegistroRequestDTO.class), any(MockMultipartFile.class)))
			.thenReturn(response);

		mockMvc.perform(multipart("/api/v1/empresas").file(empresa).file(logo))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(99))
			.andExpect(jsonPath("$.logoCargado").value(false))
			.andExpect(jsonPath("$.advertenciaLogo").value("El logo fue rechazado"));
	}

	private EmpresaRegistroRequestDTO requestValido() {
		return EmpresaRegistroRequestDTO.builder()
			.tipoIdentificacionId(7L)
			.identificacion("900123456")
			.nombre("Empresa Demo")
			.correo("empresa@correo.com")
			.celular("3001234567")
			.contacto("Contacto Demo")
			.descripcion("Empresa para pruebas")
			.personaId(15L)
			.build();
	}

	private EmpresaRegistroResponseDTO responseValido() {
		return EmpresaRegistroResponseDTO.builder()
			.id(99L)
			.tipoIdentificacionId(7L)
			.identificacion("900123456")
			.nombre("Empresa Demo")
			.correo("empresa@correo.com")
			.personaId(15L)
			.estadoId(1L)
			.logoCargado(false)
			.build();
	}

}
