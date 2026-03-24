package com.coagronet.infrastructure.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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

	private static final String BEARER_PREFIX = "Bearer ";

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
			Integer tokenVersion = claims.get("tver", Integer.class);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				// --- CAMBIO ARQUITECTÓNICO: Extraer y Setear el Contexto PRIMERO ---
				Number empresaNum = claims.get("empresaId", Number.class);

				if (empresaNum == null) {
					empresaNum = claims.get("empresa_id", Number.class);
				}

				Long empresaId = (empresaNum != null) ? empresaNum.longValue() : null;

				Integer rolId = claims.get("rolId", Integer.class);

				String rolName = (rolId != null && rolId == 1) ? "ROLE_ADMINISTRADOR_SISTEMA" : "ROLE_USUARIO_EMPRESA";

				if (empresaId == null && !rolName.equals("ROLE_ADMINISTRADOR_SISTEMA")) {
					SecurityContextHolder.clearContext();
					filterChain.doFilter(request, response);
					return;
				}

				var authorities = List.of(new SimpleGrantedAuthority(rolName));

				CustomUserDetails userDetails = new CustomUserDetails(null, username, null, empresaId, tokenVersion,
						authorities);

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// INYECTAMOS EL 452 ANTES DE TOCAR LA BASE DE DATOS
				SecurityContextHolder.getContext().setAuthentication(auth);
				// -------------------------------------------------------------------

				// AHORA SÍ tocamos la BD. Hibernate resolverá el tenant 452 exitosamente.
				Integer currentDbTokenVersion = userRepo.findTokenVersionByUsername(username).orElse(null);

				// Si la validación de negocio falla, REVERTIMOS la autenticación
				if (currentDbTokenVersion == null || jwtUtil.isTokenExpired(token)
						|| !tokenVersion.equals(currentDbTokenVersion)) {

					SecurityContextHolder.clearContext();
				}
			}
		}
		catch (JwtException | IllegalArgumentException e) {
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