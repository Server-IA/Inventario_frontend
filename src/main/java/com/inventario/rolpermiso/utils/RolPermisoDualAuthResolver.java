package com.inventario.rolpermiso.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.inventario.utils.UserEmpresaService;
import com.inventario.utils.UserRoleService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RolPermisoDualAuthResolver {

    private static final Logger logger = LoggerFactory.getLogger(RolPermisoDualAuthResolver.class);
    
    private final UserEmpresaService userEmpresaService;
    private final UserRoleService userRoleService;

	public Long resolveEmpresaId(Long empresaIdParam) {
		Authentication auth = userRoleService.getAuthentication();
		
		if (auth == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
		}
		
		String username = auth.getName();
		boolean isAdminEmpresa = userRoleService.hasRoleInAuthentication("ROLE_ADMINISTRADOR_EMPRESA");
		boolean isAdminSistema = userRoleService.hasRoleInAuthentication("ROLE_ADMINISTRADOR_SISTEMA");
		
		if (!isAdminEmpresa && !isAdminSistema) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
				"Rol no autorizado. Solo ADMINISTRADOR_EMPRESA o ADMINISTRADOR_SISTEMA pueden asignar permisos.");
		}
		
		if (isAdminSistema) {
			if (empresaIdParam == null) {
				logger.warn("ADMINISTRADOR_SISTEMA [{}] no especificó empresaId", username);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ADMINISTRADOR_SISTEMA debe especificar empresaId como parámetro");
			}
			logger.debug("ADMINISTRADOR_SISTEMA [{}] - usando empresaId del parámetro: {}", username, empresaIdParam);
			return empresaIdParam;
		}
		
		if (empresaIdParam != null) {
			logger.warn("ADMINISTRADOR_EMPRESA [{}] intentó pasar empresaId={}", username, empresaIdParam);
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
				"ADMINISTRADOR_EMPRESA no puede especificar empresaId");
		}
		
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		logger.debug("ADMINISTRADOR_EMPRESA [{}] - empresaId del contexto: {}", username, empresaId);
		return empresaId;
	}
}
