/*=============================================================================
 Nombre del archivo : JwtAuthenticationFilter.java
 Descripcion        : Filtro de autenticacion JWT y carga de autoridades del usuario.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                             |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2025-09-11 | 1.0.0   | Juan Jose Castro     | Creacion del archivo.                                                                                                              |
 | 2026-07-27 | 1.1.0   | JUAN DIAZ            | Se cargan permisos dinamicos del rol para aplicar EMPRESA_CREATE en la HU-043.1.                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.infrastructure.security;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.coagronet.infrastructure.security.service.DynamicRolePermissionService;
import com.coagronet.user.repositories.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	private final UserRepository userRepo;

	private final DynamicRolePermissionService dynamicRolePermissionService;

	private static final String BEARER_PREFIX = "Bearer ";

	private static final String ROLE_ADMIN = "ROLE_ADMINISTRADOR_SISTEMA";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(BEARER_PREFIX.length());

		try {
			Claims claims = jwtUtil.extractAllClaims(token);
			String username = claims.getSubject();

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				// 1. Extracción de variables (SOLUCIÓN AL ERROR DE COMPILACIÓN)
				String rolName = claims.get("rolName", String.class);
				Integer tokenVersion = claims.get("tver", Integer.class);

				Number userIdNum = claims.get("userId", Number.class);
				Long userId = (userIdNum != null) ? userIdNum.longValue() : null;

				Number empresaNum = claims.get("empresaId", Number.class);
				Long empresaId = (empresaNum != null) ? empresaNum.longValue() : null;

				Number rolNum = claims.get("rolId", Number.class);
				Long rolId = (rolNum != null) ? rolNum.longValue() : null;

				// 2. Verificación Multitenancy
				if (empresaId == null && !ROLE_ADMIN.equals(rolName)) {
					filterChain.doFilter(request, response);
					return;
				}

				// 3. Validación de Token Version ANTES de autenticar (Seguridad)
				// TODO: Reemplazar esta llamada a DB por un servicio de caché (ej.
				// RedisTemplate)
				Integer currentDbTokenVersion = userRepo.findTokenVersionByUsername(username).orElse(null);

				if (currentDbTokenVersion == null || !tokenVersion.equals(currentDbTokenVersion)
						|| jwtUtil.isTokenExpired(token)) {
					filterChain.doFilter(request, response);
					return; // Retorno anticipado si la versión difiere o expiró
				}

				// 4. Construcción y asignación del Security Context
				Collection<? extends GrantedAuthority> authorities = rolId != null
						? dynamicRolePermissionService.getAuthorities(empresaId, rolId, rolName)
						: List.of(new SimpleGrantedAuthority(rolName));

				// <-- PASA EL USER ID AL CONSTRUCTOR -->
				CustomUserDetails userDetails = new CustomUserDetails(userId, username, null, empresaId, rolId,
						tokenVersion, authorities);

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		} catch (JwtException | IllegalArgumentException e) {
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		return path.startsWith("/auth/v2/login") || path.equals("/auth/register") || path.equals("/auth/verify")
				|| path.equals("/auth/forgot-password") || path.equals("/auth/reset-password");
	}

}
