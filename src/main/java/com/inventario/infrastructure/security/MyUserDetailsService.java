package com.inventario.infrastructure.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.permiso.repositories.PermisoRepository;
import com.inventario.user.User;
import com.inventario.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	private final PermisoRepository permisoRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsernameWithRolesAndEstado(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		switch (user.getUsuarioEstado().getId().intValue()) {
			case 0 -> throw new DisabledException("User account is deactivated.");
			case 1 -> throw new DisabledException("User account is pending email verification.");
			case 2, 3, 4, 5 -> {
			}
			default -> throw new IllegalStateException("Unexpected value: " + user.getUsuarioEstado().getId());
		}

		Set<GrantedAuthority> authorities = new HashSet<>();

		Set<String> nombresRoles = user.getRolesAsignados()
				.stream()
				.filter(ur -> ur.getEstado() != null && ur.getEstado().getId() == 1L)
				.map(ur -> ur.getRol().getNombre())
				.collect(Collectors.toSet());

		authorities.addAll(nombresRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));

		boolean isSystemAdmin = nombresRoles.contains("ROLE_ADMINISTRADOR_SISTEMA");
		if (!isSystemAdmin && user.getPreferredEmpresa() != null) {
			List<String> permisos = permisoRepository.findPermisosByUsuarioAndEmpresa(user.getId(),
					user.getPreferredEmpresa().getId());
			authorities.addAll(permisos.stream().map(SimpleGrantedAuthority::new).toList());
		}

		Long primaryRolId = user.getRolesAsignados()
				.stream()
				.filter(ur -> ur.getEstado() != null && ur.getEstado().getId() == 1L)
				.findFirst()
				.map(ur -> ur.getRol().getId())
				.orElse(null);

		return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(),
				user.getPreferredEmpresa() != null ? user.getPreferredEmpresa().getId() : null,
				primaryRolId, user.getTokenVersion(), authorities);
	}

}