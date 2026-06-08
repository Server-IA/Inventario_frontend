package com.coagronet.user.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.exceptionHandler.custom.RecursoNoEncontradoException;
import com.coagronet.infrastructure.configuration.EmpresaTenantIdentifierResolver;
import com.coagronet.user.User;
import com.coagronet.user.dtos.AsignacionResumenAllDTO;
import com.coagronet.user.dtos.AsignacionResumenDTO;
import com.coagronet.user.dtos.UsuarioDetalleResponse;
import com.coagronet.user.dtos.UsuarioFiltroRequest;
import com.coagronet.user.dtos.UsuarioListResponse;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.user.repositories.UserSpecifications;
import com.coagronet.user.dtos.UserMinimalDTO;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioListadoService {

        private final UserRepository userRepository;
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

        @Transactional(readOnly = true)
        public List<UserMinimalDTO> listarUsuariosMinimal(UsuarioFiltroRequest filtro) {

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                boolean isSystemAdmin = auth != null && auth.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR_SISTEMA"));

                Long forcedEmpresaId = isSystemAdmin ? null : tenantResolver.resolveCurrentTenantIdentifier();

                Specification<User> spec = UserSpecifications.conFiltros(filtro, forcedEmpresaId);

                List<User> users = userRepository.findAll(spec);

                return users.stream().map(user -> new UserMinimalDTO(
                                user.getId(),
                                user.getUsername(),
                                user.getPersona() != null ? user.getPersona().getNombre() : null
                )).toList();
        }

        private UsuarioListResponse mapToResponse(User user, boolean isSystemAdmin, Long currentEmpresaId) {

                String nombreRol = (user.getPreferredRol() != null) ? user.getPreferredRol().getNombre()
                                : "Sin rol preferido";

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
                                                        estadoNombre,
                                                        ur.getIniciaContratoEn(),
                                                        ur.getFinalizaContratoEn());
                                })
                                .toList();

                return new UsuarioListResponse(
                                user.getId(),
                                user.getUsername(),
                                user.getPersona().getIdentificacion(),
                                user.getPersona().getNombre(),
                                user.getPersona().getApellido(),
                                user.getPersona().getGenero(),
                                user.getPersona().getFechaNacimiento(),
                                user.getPersona().getDireccion(),
                                user.getPersona().getCelular(),
                                nombreRol,
                                asignaciones);
        }

        @Transactional(readOnly = true)
        public UsuarioDetalleResponse obtenerUsuarioDetalle(Long requestedId) {

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                boolean isSystemAdmin = auth != null && auth.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR_SISTEMA"));

                Long forcedEmpresaId = isSystemAdmin ? null : tenantResolver.resolveCurrentTenantIdentifier();

                User user = userRepository.findById(requestedId)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", requestedId));

                boolean hasAccess = isSystemAdmin || user.getRolesAsignados().stream()
                                .anyMatch(ur -> ur.getEmpresa() != null
                                                && ur.getEmpresa().getId().equals(forcedEmpresaId));

                if (!hasAccess) {
                        throw new RecursoNoEncontradoException("Usuario", requestedId);
                }

                return mapToDetalleResponse(user, isSystemAdmin, forcedEmpresaId);
        }

        private UsuarioDetalleResponse mapToDetalleResponse(User user, boolean isSystemAdmin, Long currentEmpresaId) {

                Long rolPreferidoId = (user.getPreferredRol() != null) ? user.getPreferredRol().getId() : null;
                Long empresaPreferidaId = (user.getPreferredEmpresa() != null) ? user.getPreferredEmpresa().getId()
                                : null;
                Long personaId = (user.getPersona() != null) ? user.getPersona().getId() : null;

                var asignaciones = user.getRolesAsignados().stream()
                                .filter(ur -> isSystemAdmin ||
                                                (ur.getEmpresa() != null
                                                                && ur.getEmpresa().getId().equals(currentEmpresaId)))
                                .map(ur -> {
                                        Long usuarioRolId = ur.getId();
                                        Long empresaId = (ur.getEmpresa() != null) ? ur.getEmpresa().getId() : null;
                                        Long rolId = (ur.getRol() != null) ? ur.getRol().getId() : null;
                                        Long estadoId = (ur.getEstado() != null) ? ur.getEstado().getId() : null;

                                        return new AsignacionResumenAllDTO(
                                                        usuarioRolId,
                                                        empresaId,
                                                        rolId,
                                                        estadoId,
                                                        ur.getIniciaContratoEn(),
                                                        ur.getFinalizaContratoEn());
                                })
                                .toList();

                return new UsuarioDetalleResponse(
                                user.getUsername(),
                                personaId,
                                (user.getPersona() != null && user.getPersona().getTipoIdentificacion() != null) ? user.getPersona().getTipoIdentificacion().getId() : null,
                                (user.getPersona() != null) ? user.getPersona().getIdentificacion() : null,
                                (user.getPersona() != null) ? user.getPersona().getNombre() : null,
                                (user.getPersona() != null) ? user.getPersona().getApellido() : null,
                                (user.getPersona() != null) ? user.getPersona().getEmailPersonal() : null,
                                (user.getPersona() != null) ? user.getPersona().getGenero() : null,
                                (user.getPersona() != null) ? user.getPersona().getFechaNacimiento() : null,
                                (user.getPersona() != null) ? user.getPersona().getDireccion() : null,
                                (user.getPersona() != null) ? user.getPersona().getCelular() : null,
                                (user.getPersona() != null) ? user.getPersona().getEstrato() : null,
                                rolPreferidoId,
                                empresaPreferidaId,
                                asignaciones);
        }
}
