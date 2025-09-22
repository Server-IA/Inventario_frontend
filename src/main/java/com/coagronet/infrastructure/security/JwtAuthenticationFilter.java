package com.coagronet.infrastructure.security;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	private final UserRepository userRepo;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepo) {
		this.jwtUtil = jwtUtil;
		this.userRepo = userRepo;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);
		try {
			String username = jwtUtil.extractUsername(token);
			Integer tokenVersion = jwtUtil.extractTokenVersion(token);

			if (username != null && tokenVersion != null
					&& SecurityContextHolder.getContext().getAuthentication() == null) {
				User user = userRepo.findByUsername(username).orElse(null);
				if (user != null && !jwtUtil.isTokenExpired(token) && tokenVersion.equals(user.getTokenVersion())) { // <<<<<<
																														// comparaci�n
																														// clave

					Collection<? extends GrantedAuthority> authorities = user.getAuthorities()
						.stream()
						.map(a -> new SimpleGrantedAuthority(a.getAuthority()))
						.collect(Collectors.toSet());

					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null,
							authorities);
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		}
		catch (JwtException | IllegalArgumentException e) {
			// Token inv�lido/expirado: no autenticar
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		// Endpoints p�blicos (ajusta a tu gusto)
		return path.startsWith("/auth/v2/login") || path.equals("/auth/register") || path.equals("/auth/verify")
				|| path.equals("/auth/forgot-password") || path.equals("/auth/reset-password");
	}

}
