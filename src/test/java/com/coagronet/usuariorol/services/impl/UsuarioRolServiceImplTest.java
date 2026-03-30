package com.coagronet.usuariorol.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.coagronet.auditoria.RequestUtils;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.rol.repositories.RolRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.usuariorol.UsuarioRol;
import com.coagronet.usuariorol.mappers.UsuarioRolMapper;
import com.coagronet.usuariorol.repositories.UsuarioRolRepository;
import com.coagronet.utils.AuthenticatedUser;
import com.coagronet.utils.UserEmpresaService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UsuarioRolServiceImplTest {

    private static final Long ESTADO_ACTIVO_ID = 1L;
    private static final Long ESTADO_INACTIVO_ID = 2L;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    @Mock
    private UsuarioRolMapper usuarioRolMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private EmpresaRolRepository empresaRolRepository;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @Mock
    private RequestUtils requestUtils;

    @Mock
    private UserEmpresaService userEmpresaService;

    @InjectMocks
    private UsuarioRolServiceImpl usuarioRolService;

    @Test
    void toggleEstadoForCurrentEmpresa_changesActiveToInactive() {
        Long usuarioRolId = 101L;
        Long empresaId = 10L;

        User currentUser = new User();
        currentUser.setId(99L);

        Estado activo = new Estado();
        activo.setId(ESTADO_ACTIVO_ID);

        Estado inactivo = new Estado();
        inactivo.setId(ESTADO_INACTIVO_ID);

        UsuarioRol entity = new UsuarioRol();
        entity.setId(usuarioRolId);
        entity.setEstado(activo);

        when(authenticatedUser.getCurrentUser()).thenReturn(currentUser);
        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(usuarioRolRepository.findByIdAndEmpresaIdAndDeletedAtIsNullAndEstadoIdNot(usuarioRolId, empresaId,
                ESTADO_INACTIVO_ID)).thenReturn(Optional.of(entity));
        when(estadoRepository.findById(requireNonNull(ESTADO_INACTIVO_ID))).thenReturn(Optional.of(inactivo));

        usuarioRolService.toggleEstadoForCurrentEmpresa(usuarioRolId);

        assertThat(entity.getEstado().getId()).isEqualTo(ESTADO_INACTIVO_ID);
        assertThat(entity.getUpdatedBy()).isEqualTo(currentUser);
        assertThat(entity.getUpdatedAt()).isNotNull();
        verify(usuarioRolRepository).save(entity);
    }

    @Test
    void toggleEstadoForCurrentEmpresa_changesInactiveToActive() {
        Long usuarioRolId = 202L;
        Long empresaId = 20L;

        User currentUser = new User();
        currentUser.setId(88L);

        Estado activo = new Estado();
        activo.setId(ESTADO_ACTIVO_ID);

        Estado inactivo = new Estado();
        inactivo.setId(ESTADO_INACTIVO_ID);

        UsuarioRol entity = new UsuarioRol();
        entity.setId(usuarioRolId);
        entity.setEstado(inactivo);

        when(authenticatedUser.getCurrentUser()).thenReturn(currentUser);
        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(usuarioRolRepository.findByIdAndEmpresaIdAndDeletedAtIsNullAndEstadoIdNot(usuarioRolId, empresaId,
                ESTADO_INACTIVO_ID)).thenReturn(Optional.of(entity));
        when(estadoRepository.findById(requireNonNull(ESTADO_ACTIVO_ID))).thenReturn(Optional.of(activo));

        usuarioRolService.toggleEstadoForCurrentEmpresa(usuarioRolId);

        assertThat(entity.getEstado().getId()).isEqualTo(ESTADO_ACTIVO_ID);
        assertThat(entity.getUpdatedBy()).isEqualTo(currentUser);
        assertThat(entity.getUpdatedAt()).isNotNull();
        verify(usuarioRolRepository).save(entity);
    }

    @Test
    void toggleEstadoForCurrentEmpresa_blocksAccessToOtherEmpresa() {
        Long usuarioRolId = 303L;
        Long empresaDelToken = 30L;

        when(authenticatedUser.getCurrentUser()).thenReturn(new User());
        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaDelToken);
        when(usuarioRolRepository.findByIdAndEmpresaIdAndDeletedAtIsNullAndEstadoIdNot(usuarioRolId, empresaDelToken,
                ESTADO_INACTIVO_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> usuarioRolService.toggleEstadoForCurrentEmpresa(usuarioRolId));

        assertThat(ex.getMessage()).contains("empresa actual");
        verifyNoInteractions(estadoRepository);
    }

}