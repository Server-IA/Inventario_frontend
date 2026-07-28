/*=============================================================================
 Nombre del archivo : JwtAuthenticationFilterEmpresaReadPermissionTest.java
 Descripcion        : Prueba de carga del permiso dinamico para listar empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-07-27 | 1.0.0   | JUAN DIAZ            | Creacion de prueba para comprobar EMPRESA_READ_ALL requerido por la HU-043.2.                                                     |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.coagronet.infrastructure.security.service.DynamicRolePermissionService;
import com.coagronet.user.repositories.UserRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterEmpresaReadPermissionTest {

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private UserRepository userRepository;

	@Mock
	private DynamicRolePermissionService dynamicRolePermissionService;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void doFilter_cargaAutoridadEmpresaReadAll() throws Exception {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userRepository,
				dynamicRolePermissionService);
		Claims claims = mock(Claims.class);
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);
		request.addHeader("Authorization", "Bearer token-valido");

		when(jwtUtil.extractAllClaims("token-valido")).thenReturn(claims);
		when(claims.getSubject()).thenReturn("usuario@coagronet.com");
		when(claims.get("rolName", String.class)).thenReturn("ROL_EMPRESA");
		when(claims.get("tver", Integer.class)).thenReturn(3);
		when(claims.get("userId", Number.class)).thenReturn(10L);
		when(claims.get("empresaId", Number.class)).thenReturn(20L);
		when(claims.get("rolId", Number.class)).thenReturn(30L);
		when(userRepository.findTokenVersionByUsername("usuario@coagronet.com")).thenReturn(Optional.of(3));
		when(jwtUtil.isTokenExpired("token-valido")).thenReturn(false);
		Collection<GrantedAuthority> authorities = List.of(
				new SimpleGrantedAuthority("ROL_EMPRESA"),
				new SimpleGrantedAuthority("EMPRESA_READ_ALL"));
		when(dynamicRolePermissionService.getAuthorities(20L, 30L, "ROL_EMPRESA")).thenReturn(authorities);

		filter.doFilter(request, response, chain);

		assertTrue(SecurityContextHolder.getContext()
			.getAuthentication()
			.getAuthorities()
			.stream()
			.anyMatch(authority -> "EMPRESA_READ_ALL".equals(authority.getAuthority())));
	}

}
