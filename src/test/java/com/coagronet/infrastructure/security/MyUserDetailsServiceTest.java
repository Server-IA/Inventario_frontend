package com.coagronet.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.permiso.repositories.PermisoRepository;
import com.coagronet.rol.Rol;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.usuarioEstado.UsuarioEstado;
import com.coagronet.usuariorol.UsuarioRol;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PermisoRepository permisoRepository;

        @InjectMocks
        private MyUserDetailsService myUserDetailsService;

        @Test
        void loadUserByUsername_deniesAccessWhenUserIsDeactivated() {
                String username = "desactivado@coagronet.com";

                User user = User.builder()
                                .id(1L)
                                .username(username)
                                .usuarioEstado(UsuarioEstado.builder().id(UsuarioEstado.ID_DESACTIVADO).build())
                                .build();

                when(userRepository.findByUsernameWithRolesAndEstado(username)).thenReturn(Optional.of(user));

                assertThrows(DisabledException.class, () -> myUserDetailsService.loadUserByUsername(username));
                verifyNoInteractions(permisoRepository);
        }

        @Test
        void loadUserByUsername_loadsRoleAndPermissionAuthoritiesForPreferredEmpresa() {
                String username = "admin.empresa@coagronet.com";
                Long empresaId = 5L;

                Estado estadoActivo = Estado.builder().id(1L).nombre("Activo").build();
                Rol role = Rol.builder().id(7L).nombre("ROLE_ADMINISTRADOR_EMPRESA").estado(estadoActivo).build();
                UsuarioRol contrato = UsuarioRol.builder().rol(role).estado(estadoActivo).build();

                User user = User.builder()
                                .id(2L)
                                .username(username)
                                .password("pwd")
                                .usuarioEstado(UsuarioEstado.builder().id(UsuarioEstado.ID_ACTIVADO_CON_EMPRESA)
                                                .build())
                                .preferredEmpresa(Empresa.builder().id(empresaId).build())
                                .rolesAsignados(Set.of(contrato))
                                .build();

                when(userRepository.findByUsernameWithRolesAndEstado(username)).thenReturn(Optional.of(user));
                when(permisoRepository.findPermisosByUsuarioAndEmpresa(2L, empresaId))
                                .thenReturn(List.of("USUARIO_ROL_INACTIVATE"));

                CustomUserDetails loaded = (CustomUserDetails) myUserDetailsService.loadUserByUsername(username);

                Set<String> authorities = loaded.getAuthorities().stream()
                                .map(a -> a.getAuthority())
                                .collect(java.util.stream.Collectors.toSet());

                assertThat(authorities).contains("ROLE_ADMINISTRADOR_EMPRESA", "USUARIO_ROL_INACTIVATE");
                verify(permisoRepository).findPermisosByUsuarioAndEmpresa(2L, empresaId);
        }
}
