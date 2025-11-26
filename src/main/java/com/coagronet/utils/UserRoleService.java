package com.coagronet.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.coagronet.infrastructure.security.JwtUtil;
import com.coagronet.rol.Rol;
import com.coagronet.rol.repositories.RolRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleService {

	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtUtil jwtUtil;

	private final RolRepository roleRepository;

	/**
	 * Extrae el JWT de la cabecera Authorization, valida el token, y retorna el
	 * nombre
	 * del rol asociado (como String).
	 */
	public String getRoleFromCurrentRequest() {
		String token = resolveTokenFromHeader();
		Claims claims = parseClaims(token);

		Long roleId = claims.get("rolId", Long.class);
		if (roleId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sin claim 'rolId'");
		}

		return roleRepository.findById(roleId)
				.map(Rol::getNombre)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
	}

	private String resolveTokenFromHeader() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attrs == null) {
			log.error("No se pudo obtener RequestContext");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en contexto de solicitud");
		}

		HttpServletRequest request = attrs.getRequest();
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (!StringUtils.hasText(header) || !header.startsWith(BEARER_PREFIX)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta o es inválida la cabecera Authorization");
		}

		return header.substring(BEARER_PREFIX.length());
	}

	private Claims parseClaims(@NotNull String token) {
		try {
			return jwtUtil.extractAllClaims(token);
		} catch (JwtException ex) {
			log.warn("JWT inválido o expirado: {}", ex.getMessage());
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT inválido o expirado");
		}
	}

}
