package com.inventario.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.inventario.infrastructure.security.JwtUtil;
import com.inventario.rol.Rol;
import com.inventario.rol.repositories.RolRepository;

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

	/**
	 * Verifica si el usuario autenticado tiene un rol específico.
	 * 
	 * @param roleName Nombre del rol a verificar (ej: "ROLE_ADMINISTRADOR_SISTEMA")
	 * @return true si el usuario tiene el rol, false en caso contrario
	 */
	public boolean hasRole(String roleName) {
		try {
			String currentRole = getRoleFromCurrentRequest();
			return currentRole != null && currentRole.equals(roleName);
		} catch (ResponseStatusException ex) {
			log.debug("No se pudo verificar rol desde JWT: {}", ex.getMessage());
			return false;
		}
	}

	/**
	 * Obtiene la Authentication del contexto de seguridad actual de manera centralizada.
	 * 
	 * @return Authentication del contexto actual, null si no está autenticado
	 */
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * Verifica si el usuario autenticado tiene un rol específico basándose en las GrantedAuthorities.
	 * Útil cuando los roles están en el Authentication como GrantedAuthority.
	 * 
	 * @param roleName Nombre del rol a verificar (ej: "ROLE_ADMINISTRADOR_SISTEMA")
	 * @return true si el usuario tiene el rol en sus authorities, false en caso contrario
	 */
	public boolean hasRoleInAuthentication(String roleName) {
		Authentication auth = getAuthentication();
		if (auth == null) {
			return false;
		}
		return auth.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.anyMatch(authority -> authority.equals(roleName));
	}

}
