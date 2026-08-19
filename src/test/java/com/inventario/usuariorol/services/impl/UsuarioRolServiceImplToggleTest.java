package com.inventario.usuariorol.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventario.auditoria.RequestUtils;
import com.inventario.empresa.repositories.EmpresaRepository;
import com.inventario.empresarol.repositories.EmpresaRolRepository;
import com.inventario.estado.Estado;
import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.rol.repositories.RolRepository;
import com.inventario.user.User;
import com.inventario.user.repositories.UserRepository;
import com.inventario.usuariorol.UsuarioRol;
import com.inventario.usuariorol.mappers.UsuarioRolMapper;
import com.inventario.usuariorol.repositories.UsuarioRolRepository;
import com.inventario.utils.AuthenticatedUser;
import com.inventario.utils.UserEmpresaService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UsuarioRolServiceImplToggleTest {

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
    private UsuarioRolServiceImpl service;

    @Test
    void toggleEstado_reactivatesWhenCurrentEstadoIsInactivo() {
        Long id = 100L;
        Long empresaId = 10L;

        User currentUser = new User();
        currentUser.setId(999L);

        Estado inactivo = new Estado();
        inactivo.setId(2L);

        Estado activo = new Estado();
        activo.setId(1L);

        UsuarioRol entity = new UsuarioRol();
        entity.setId(id);
        entity.setEstado(inactivo);

        when(authenticatedUser.getCurrentUser()).thenReturn(currentUser);
        when(usuarioRolRepository.findByIdAndEmpresaIdAndDeletedAtIsNull(id, empresaId)).thenReturn(Optional.of(entity));
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(activo));

        service.toggleEstado(id, empresaId);

        assertThat(entity.getEstado().getId()).isEqualTo(1L);
        verify(usuarioRolRepository).save(entity);
    }

    @Test
    void toggleEstado_inactivatesWhenCurrentEstadoIsActivo() {
        Long id = 101L;
        Long empresaId = 11L;

        User currentUser = new User();
        currentUser.setId(999L);

        Estado activo = new Estado();
        activo.setId(1L);

        Estado inactivo = new Estado();
        inactivo.setId(2L);

        UsuarioRol entity = new UsuarioRol();
        entity.setId(id);
        entity.setEstado(activo);

        when(authenticatedUser.getCurrentUser()).thenReturn(currentUser);
        when(usuarioRolRepository.findByIdAndEmpresaIdAndDeletedAtIsNull(id, empresaId)).thenReturn(Optional.of(entity));
        when(estadoRepository.findById(2L)).thenReturn(Optional.of(inactivo));

        service.toggleEstado(id, empresaId);

        assertThat(entity.getEstado().getId()).isEqualTo(2L);
        verify(usuarioRolRepository).save(entity);
    }

    @Test
    void toggleEstadoForCurrentEmpresa_reactivatesWhenCurrentEstadoIsInactivo() {
        Long id = 200L;
        Long empresaId = 20L;

        User currentUser = new User();
        currentUser.setId(999L);

        Estado inactivo = new Estado();
        inactivo.setId(2L);

        Estado activo = new Estado();
        activo.setId(1L);

        UsuarioRol entity = new UsuarioRol();
        entity.setId(id);
        entity.setEstado(inactivo);

        when(authenticatedUser.getCurrentUser()).thenReturn(currentUser);
        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(usuarioRolRepository.findByIdAndEmpresaIdAndDeletedAtIsNull(id, empresaId)).thenReturn(Optional.of(entity));
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(activo));

        service.toggleEstadoForCurrentEmpresa(id);

        assertThat(entity.getEstado().getId()).isEqualTo(1L);
        verify(usuarioRolRepository).save(entity);
    }

    @Test
    void toggleEstadoForCurrentEmpresa_inactivatesWhenCurrentEstadoIsActivo() {
        Long id = 201L;
        Long empresaId = 21L;

        User currentUser = new User();
        currentUser.setId(999L);

        Estado activo = new Estado();
        activo.setId(1L);

        Estado inactivo = new Estado();
        inactivo.setId(2L);

        UsuarioRol entity = new UsuarioRol();
        entity.setId(id);
        entity.setEstado(activo);

        when(authenticatedUser.getCurrentUser()).thenReturn(currentUser);
        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(usuarioRolRepository.findByIdAndEmpresaIdAndDeletedAtIsNull(id, empresaId)).thenReturn(Optional.of(entity));
        when(estadoRepository.findById(2L)).thenReturn(Optional.of(inactivo));

        service.toggleEstadoForCurrentEmpresa(id);

        assertThat(entity.getEstado().getId()).isEqualTo(2L);
        verify(usuarioRolRepository).save(entity);
    }

    @Test
    void toggleEstado_throwsNotFoundWhenRecordIsMissing() {
        Long id = 500L;
        Long empresaId = 50L;

        when(authenticatedUser.getCurrentUser()).thenReturn(new User());
        when(usuarioRolRepository.findByIdAndEmpresaIdAndDeletedAtIsNull(id, empresaId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.toggleEstado(id, empresaId));
    }

    @Test
    void toggleEstadoForCurrentEmpresa_throwsNotFoundWhenRecordIsMissing() {
        Long id = 501L;
        Long empresaId = 51L;

        when(authenticatedUser.getCurrentUser()).thenReturn(new User());
        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(usuarioRolRepository.findByIdAndEmpresaIdAndDeletedAtIsNull(id, empresaId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.toggleEstadoForCurrentEmpresa(id));
    }
}
