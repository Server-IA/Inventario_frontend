package com.coagronet.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;

	public User getAuthenticatedUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
	}

}
