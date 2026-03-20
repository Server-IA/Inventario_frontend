package com.coagronet.rolpermiso.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

/**
 * Resuelve automáticamente qué empresaId usar según el rol del usuario autenticado.
 * 
 * Implementa validaciones de seguridad para:
 * - ADMINISTRADOR_EMPRESA: Obtiene empresaId del contexto (rechaza parámetro)
 * - ADMINISTRADOR_SISTEMA: Requiere empresaId como parámetro
 * 
 * Lógica:
 * - ADMINISTRADOR_EMPRESA + empresaIdParam != null → Lanza ForbiddenException
 * - ADMINISTRADOR_EMPRESA + empresaIdParam == null → Obtiene del contexto
 * - ADMINISTRADOR_SISTEMA + empresaIdParam != null → Usa el parámetro
 * - ADMINISTRADOR_SISTEMA + empresaIdParam == null → Lanza BadRequestException
 */
@Component
@RequiredArgsConstructor
public class RolPermisoDualAuthResolver {

    private static final Logger logger = LoggerFactory.getLogger(RolPermisoDualAuthResolver.class);
    
    private final UserEmpresaService userEmpresaService;

    /**
     * Resuelve el empresaId según el contexto de autenticación y el parámetro enviado.
     * 
     * @param empresaIdParam El parámetro empresaId enviado por la API (solo para ADMIN_SISTEMA)
     * @return El empresaId a usar: parámetro si es ADMIN_SISTEMA, contexto si es ADMIN_EMPRESA
     * 
     *
     */
	public Long resolveEmpresaId(Long empresaIdParam) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
		}
		
		boolean isAdminEmpresa = hasRole(auth, "ROLE_ADMINISTRADOR_EMPRESA");
		boolean isAdminSistema = hasRole(auth, "ROLE_ADMINISTRADOR_SISTEMA");
		
		if (!isAdminEmpresa && !isAdminSistema) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
				"Rol no autorizado. Solo ADMINISTRADOR_EMPRESA o ADMINISTRADOR_SISTEMA pueden asignar permisos.");
		}
		
		// ============================================================
		// CASO 1: ADMINISTRADOR_EMPRESA
		// ============================================================
		if (isAdminEmpresa) {
			
			// ADMIN_EMPRESA intentando pasar empresaId
			if (empresaIdParam != null) {
				String username = auth.getName();
				logger.warn(
					"⚠️ INTENTO DE ESCALADA DE PRIVILEGIOS: " +
					"ADMINISTRADOR_EMPRESA [{}] intentó pasar empresaId={}. " +
					"Solo ADMINISTRADOR_SISTEMA puede especificar empresaId.",
					username, 
					empresaIdParam
				);
				throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"ADMINISTRADOR_EMPRESA no puede especificar empresaId. " +
					"Se usa automáticamente la empresa del contexto actual. " +
					"Solo ADMINISTRADOR_SISTEMA puede pasar este parámetro."
				);
			}
			
			// ADMIN_EMPRESA sin parámetro: obtener del contexto
			Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
			logger.debug("ADMINISTRADOR_EMPRESA [{}] - empresaId obtenido del contexto: {}",
				auth.getName(), empresaId);
			return empresaId;
		}
		
		// ============================================================
		// CASO 2: ADMINISTRADOR_SISTEMA
		// ============================================================
		if (isAdminSistema) {
			
			// ERROR: ADMIN_SISTEMA no pasó empresaId
			if (empresaIdParam == null) {
				logger.warn(
					"SOLICITUD INVÁLIDA: ADMINISTRADOR_SISTEMA [{}] " +
					"no especificó empresaId. Parámetro requerido.",
					auth.getName()
				);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ADMINISTRADOR_SISTEMA DEBE especificar empresaId como parámetro. " +
					"Ejemplo: POST /rol/45/permisos?empresaId=7"
				);
			}
			
			// ADMIN_SISTEMA con parámetro: usarlo directamente
			logger.debug("ADMINISTRADOR_SISTEMA [{}] - usando empresaId del parámetro: {}",
				auth.getName(), empresaIdParam);
			return empresaIdParam;
		}
		
		// No debería llegar aquí, pero por seguridad
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Estado de autenticación inválido");
	}

    /**
     * Verifica si el usuario autenticado tiene un role específico.
     * 
     * @param auth Authentication del contexto
     * @param role Nombre del rol a verificar (ej: "ROLE_ADMINISTRADOR_EMPRESA")
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .anyMatch(authority -> authority.equals(role));
    }

    /**
     * Verifica si el usuario autenticado es ADMINISTRADOR_SISTEMA.
     * 
     * @return true si es ADMIN_SISTEMA, false en caso contrario
     */
    public boolean isAdminSistema() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && hasRole(auth, "ROLE_ADMINISTRADOR_SISTEMA");
    }

    /**
     * Verifica si el usuario autenticado es ADMINISTRADOR_EMPRESA.
     * 
     * @return true si es ADMIN_EMPRESA, false en caso contrario
     */
    public boolean isAdminEmpresa() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && hasRole(auth, "ROLE_ADMINISTRADOR_EMPRESA");
    }

    /**
     * Obtiene el username del usuario autenticado.
     * Útil para logs y auditoría.
     * 
     * @return Username del usuario autenticado o "UNKNOWN" si no está autenticado
     */
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "UNKNOWN";
    }
}
