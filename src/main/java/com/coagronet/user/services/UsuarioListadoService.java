package com.coagronet.user.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.infrastructure.configuration.EmpresaTenantIdentifierResolver;
import com.coagronet.rol.Rol;
import com.coagronet.rol.repositories.RolRepository;
import com.coagronet.user.User;
import com.coagronet.user.dtos.AsignacionResumenDTO;
import com.coagronet.user.dtos.UsuarioFiltroRequest;
import com.coagronet.user.dtos.UsuarioListResponse;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.user.repositories.UserSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioListadoService {

        private final UserRepository userRepository;
        private final RolRepository rolRepository;
        private final EmpresaTenantIdentifierResolver tenantResolver;

        @Transactional(readOnly = true)
        public Page<UsuarioListResponse> listarUsuarios(UsuarioFiltroRequest filtro, Pageable pageable) {

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                boolean isSystemAdmin = auth != null && auth.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR_SISTEMA"));

                Long forcedEmpresaId = isSystemAdmin ? null : tenantResolver.resolveCurrentTenantIdentifier();

                Specification<User> spec = UserSpecifications.conFiltros(filtro, forcedEmpresaId);

                Page<User> usersPage = userRepository.findAll(spec, pageable);

                return usersPage.map(user -> mapToResponse(user, isSystemAdmin, forcedEmpresaId));
        }

        private UsuarioListResponse mapToResponse(User user, boolean isSystemAdmin, Long currentEmpresaId) {

                String nombreRol = (user.getPreferredRolId() != null) ? rolRepository.findById(user.getPreferredRolId())
                                .map(Rol::getNombre).orElse("Sin rol preferido") : "Sin rol preferido";

                var asignaciones = user.getRolesAsignados().stream()
                                .filter(ur -> isSystemAdmin ||
                                                (ur.getEmpresa() != null
                                                                && ur.getEmpresa().getId().equals(currentEmpresaId)))
                                .map(ur -> {
                                        Long empresaId = (ur.getEmpresa() != null) ? ur.getEmpresa().getId() : null;
                                        String empresaNombre = (ur.getEmpresa() != null) ? ur.getEmpresa().getNombre()
                                                        : "Sin empresa";
                                        String rolNombre = (ur.getRol() != null) ? ur.getRol().getNombre()
                                                        : "Desconocido";
                                        String estadoNombre = (ur.getEstado() != null) ? ur.getEstado().getNombre()
                                                        : "Desconocido";

                                        return new AsignacionResumenDTO(
                                                        empresaId,
                                                        empresaNombre,
                                                        rolNombre,
                                                        estadoNombre);
                                })
                                .toList();

                return new UsuarioListResponse(
                                user.getId(),
                                user.getUsername(),
                                user.getPersona().getIdentificacion(),
                                user.getPersona().getNombre(),
                                user.getPersona().getApellido(),
                                user.getPersona().getCelular(),
                                nombreRol,
                                asignaciones);
        }
}
