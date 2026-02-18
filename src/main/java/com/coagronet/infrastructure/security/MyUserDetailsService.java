package com.coagronet.infrastructure.security;

import com.coagronet.permiso.repositories.PermisoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PermisoRepository permisoRepository;

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
			case 5:
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + user.getUsuarioEstado().getId());
		}

		Set<GrantedAuthority> authorities = new HashSet<>();

		// Mantener roles actuales (compatibilidad)
		authorities.addAll(
				user.getRoles().stream()
						.map(r -> new SimpleGrantedAuthority(r.getNombre()))
						.collect(Collectors.toSet())
		);

		// Si NO es admin sistema → cargar permisos por empresa
		boolean isSystemAdmin = user.getRoles().stream()
				.anyMatch(r -> r.getNombre().equals("ROLE_ADMINISTRADOR_SISTEMA"));


		if (!isSystemAdmin && user.getPreferredEmpresaId() != null) {

			List<String> permisos = permisoRepository
					.findPermisosByUsuarioAndEmpresa(
							user.getId(),
							user.getPreferredEmpresaId()
					);

			authorities.addAll(
					permisos.stream()
							.map(SimpleGrantedAuthority::new)
							.toList()
			);
		}

		user.setAuthorities(authorities);
		System.out.println("Authorities cargadas: " + authorities);
		return user;
	}

}
