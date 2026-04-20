package com.coagronet.infrastructure.security.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.rolpermiso.repositories.RolPermisoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DynamicRolePermissionService {

	private final RolPermisoRepository rolPermisoRepository;

	@Cacheable(value = "tenantRoleAuthorities",
			key = "T(java.lang.String).valueOf(#empresaId).concat('-').concat(#rolId)")
	@Transactional(readOnly = true)
	public Collection<GrantedAuthority> getAuthorities(Long empresaId, Long rolId, String rolName) {

		Collection<GrantedAuthority> authorities = rolPermisoRepository
			.findByRolIdAndEmpresaIdWithPermisos(rolId, empresaId)
			.stream()
			.map(rp -> new SimpleGrantedAuthority(rp.getPermiso().getAutoridad()))
			.collect(Collectors.toSet());

		if (rolName != null && !rolName.isBlank()) {
			authorities.add(new SimpleGrantedAuthority(rolName));
		}

		return authorities;
	}

}