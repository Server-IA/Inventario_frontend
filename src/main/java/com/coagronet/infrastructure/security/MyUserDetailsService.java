package com.coagronet.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsernameWithRolesAndEstado(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		// Verificación de estado (igual que antes)
		switch (user.getUsuarioEstado().getId().intValue()) {
		case 0:
			throw new DisabledException("User account is deactivated.");
		case 1:
			throw new DisabledException("User account is pending email verification.");
		case 2:
		case 3:
		case 4:
			break;
		default:
			throw new IllegalStateException("Unexpected value: " + user.getUsuarioEstado().getId());
		}

		return user; //Aqui el cambio importante
	}
}
