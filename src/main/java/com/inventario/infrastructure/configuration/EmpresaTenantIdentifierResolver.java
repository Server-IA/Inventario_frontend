package com.inventario.infrastructure.configuration;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.inventario.infrastructure.security.CustomUserDetails;

@Component
public class EmpresaTenantIdentifierResolver implements CurrentTenantIdentifierResolver<Long> {

	private static final Long SYSTEM_TENANT_ID = 1L;

	@Override
	public Long resolveCurrentTenantIdentifier() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {

			if (auth.getPrincipal() instanceof CustomUserDetails user) {
				if (user.empresaId() != null) {
					return user.empresaId();
				}
				else if (user.getAuthorities()
					.stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR_SISTEMA"))) {

					return SYSTEM_TENANT_ID;
				}
				else {
					throw new IllegalStateException(
							"Falla de Arquitectura: El CustomUserDetails del usuario no contiene un 'empresaId' válido.");
				}
			}
			else {

				throw new IllegalStateException(
						"Falla de Arquitectura: El Principal no es una instancia de CustomUserDetails. Clase actual: "
								+ auth.getPrincipal().getClass().getName());
			}
		}

		return SYSTEM_TENANT_ID;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return false;
	}

}
