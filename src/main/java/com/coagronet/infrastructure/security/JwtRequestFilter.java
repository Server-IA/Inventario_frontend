package com.coagronet.infrastructure.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component @RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	@Autowired @Qualifier("handlerExceptionResolver")
	private HandlerExceptionResolver resolver;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;

		if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			jwt = authHeader.substring(7);

			if (jwtService.isTokenValid(jwt)) {

				userEmail = jwtService.extractUsername(jwt);
				Integer rolId = jwtService.extractRoleId(jwt);

				List<GrantedAuthority> authorities = getAuthoritiesFromRoleId(rolId);

				UserDetails userDetails = new User(userEmail, "", authorities);

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}

			filterChain.doFilter(request, response);

		} catch (Exception ex) {
			resolver.resolveException(request, response, null, ex);
		}
	}

	private List<GrantedAuthority> getAuthoritiesFromRoleId(Integer rolId) {
		List<GrantedAuthority> authorities = new ArrayList<>();

		if (rolId != null) {
			switch (rolId) {
			case 1:
				authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR_SISTEMA"));
				break;
			case 2:
				authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR_EMPRESA"));
				break;
			case 3:
				authorities.add(new SimpleGrantedAuthority("ROLE_GERENTE"));
				break;
			default:
				authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
				break;
			}
		}
		return authorities;
	}

}
