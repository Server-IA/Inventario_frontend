package com.inventario.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.inventario.user.User;
import com.inventario.user.repositories.UserRepository;

@Component
public class AuthenticatedUser {

	private final UserRepository userRepo;

	public AuthenticatedUser(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated())
			return null;

		// Esto funciona aunque el principal sea String / Jwt / UserDetails
		String username = auth.getName();

		return userRepo.findByUsername(username).orElse(null);
	}

	public Long getCurrentUserId() {
		User user = getCurrentUser();
		return (user != null) ? user.getId() : null;
	}
}
