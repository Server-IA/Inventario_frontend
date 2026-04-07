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
import org.springframework.security.core.GrantedAuthority;

import com.coagronet.permiso.repositories.PermisoRepository;
import com.coagronet.rol.Rol;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.usuarioEstado.UsuarioEstado;

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

        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setUsuarioEstado(new UsuarioEstado(0L));

        when(userRepository.findByUsernameWithRolesAndEstado(username)).thenReturn(Optional.of(user));

        assertThrows(DisabledException.class, () -> myUserDetailsService.loadUserByUsername(username));
        verifyNoInteractions(permisoRepository);
    }

    @Test
    void loadUserByUsername_loadsRoleAndPermissionAuthoritiesForPreferredEmpresa() {
        String username = "admin.empresa@coagronet.com";
        Long empresaId = 5L;

        Rol role = Rol.builder().id(7L).nombre("ROLE_ADMINISTRADOR_EMPRESA").build();

        User user = new User();
        user.setId(2L);
        user.setUsername(username);
        user.setRoles(Set.of(role));
        user.setUsuarioEstado(new UsuarioEstado(4L));
        user.setPreferredEmpresaId(empresaId);

        when(userRepository.findByUsernameWithRolesAndEstado(username)).thenReturn(Optional.of(user));
        when(permisoRepository.findPermisosByUsuarioAndEmpresa(2L, empresaId))
                .thenReturn(List.of("USUARIO_ROL_INACTIVATE"));

        User loaded = (User) myUserDetailsService.loadUserByUsername(username);

        Set<String> authorities = loaded.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());

        assertThat(authorities)
                .contains("ROLE_ADMINISTRADOR_EMPRESA", "USUARIO_ROL_INACTIVATE");
        verify(permisoRepository).findPermisosByUsuarioAndEmpresa(2L, empresaId);
    }

}