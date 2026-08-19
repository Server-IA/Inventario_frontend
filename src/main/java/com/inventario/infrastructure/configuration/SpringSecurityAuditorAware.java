package com.inventario.infrastructure.configuration;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.inventario.infrastructure.security.CustomUserDetails;
import com.inventario.user.User;

@Component("auditorAware")
public class SpringSecurityAuditorAware implements AuditorAware<User> {

	@Override
	public Optional<User> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication.getPrincipal() instanceof String) {
			return Optional.empty();
		}

		if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
			Long userId = userDetails.id();

			if (userId != null) {
				User userReference = new User();
				userReference.setId(userId);
				return Optional.of(userReference);
			}
		}

		return Optional.empty();
	}
}