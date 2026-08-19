package com.inventario.utils;

import org.springframework.stereotype.Service;

import com.inventario.infrastructure.security.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class UserEmpresaService {

	private final JwtUtil jwtUtil;

	public Long getEmpresaIdFromCurrentRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
			.getRequest();
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new IllegalStateException("No se encontró el token JWT en la solicitud.");
		}
		String jwtToken = authHeader.substring(7);
		Claims claims = jwtUtil.extractAllClaims(jwtToken);
		return claims.get("empresaId", Long.class);
	}

}
