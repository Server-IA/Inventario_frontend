package com.inventario.usuariorol.utils;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.rol.Rol;
import com.inventario.user.User;
import com.inventario.user.repositories.UserRepository;
import com.inventario.usuariorol.UsuarioRol;
import com.inventario.usuariorol.repositories.UsuarioRolRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioRolContratoServiceTest {

        private static final Long ESTADO_ACTIVO_ID = 1L;
        private static final Long ESTADO_INACTIVO_ID = 2L;

        @Mock
        private UsuarioRolRepository usuarioRolRepository;

        @Mock
        private EstadoRepository estadoRepository;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private UsuarioRolContratoService usuarioRolContratoService;

        @Test
        void procesarContratos_activatesPendingAssignments() {
                Estado activo = new Estado();
                activo.setId(ESTADO_ACTIVO_ID);

                Estado inactivo = new Estado();
                inactivo.setId(ESTADO_INACTIVO_ID);

                UsuarioRol pendiente = new UsuarioRol();
                pendiente.setId(10L);
                pendiente.setEstado(inactivo);
                pendiente.setIniciaContratoEn(OffsetDateTime.now().minusDays(1));
                pendiente.setUser(buildUser(100L));
                pendiente.setEmpresa(buildEmpresa(1L));
                pendiente.setRol(buildRol(10L));

                when(usuarioRolRepository.findByEstadoInactivoYFechaActivacion(eq(ESTADO_INACTIVO_ID), any()))
                                .thenReturn(List.of(pendiente));
                when(usuarioRolRepository.findByEstadoActivoYFechaFinalizacionPasada(eq(ESTADO_ACTIVO_ID), any()))
                                .thenReturn(List.of());
                when(estadoRepository.findById(requireNonNull(ESTADO_ACTIVO_ID))).thenReturn(Optional.of(activo));

                usuarioRolContratoService.procesarContratos();

                assertThat(pendiente.getEstado().getId()).isEqualTo(ESTADO_ACTIVO_ID);
                assertThat(pendiente.getUpdatedAt()).isNotNull();
                verify(usuarioRolRepository).save(pendiente);
        }

        @Test
        void procesarContratos_inactivatesExpiredAssignments() {
                Estado activo = new Estado();
                activo.setId(ESTADO_ACTIVO_ID);

                Estado inactivo = new Estado();
                inactivo.setId(ESTADO_INACTIVO_ID);

                UsuarioRol expirado = new UsuarioRol();
                expirado.setId(20L);
                expirado.setEstado(activo);
                expirado.setFinalizaContratoEn(OffsetDateTime.now().minusDays(1));
                expirado.setUser(buildUser(200L));
                expirado.setEmpresa(buildEmpresa(2L));
                expirado.setRol(buildRol(20L));

                when(usuarioRolRepository.findByEstadoInactivoYFechaActivacion(eq(ESTADO_INACTIVO_ID), any()))
                                .thenReturn(List.of());
                when(usuarioRolRepository.findByEstadoActivoYFechaFinalizacionPasada(eq(ESTADO_ACTIVO_ID), any()))
                                .thenReturn(List.of(expirado));
                when(estadoRepository.findById(requireNonNull(ESTADO_INACTIVO_ID))).thenReturn(Optional.of(inactivo));

                usuarioRolContratoService.procesarContratos();

                assertThat(expirado.getEstado().getId()).isEqualTo(ESTADO_INACTIVO_ID);
                assertThat(expirado.getUpdatedAt()).isNotNull();
                verify(usuarioRolRepository).save(expirado);
                verifyNoInteractions(userRepository);
        }

        @Test
        void procesarContratos_reassignsPreferredAssignment_whenExpiredWasPreferred() {
                Estado activo = new Estado();
                activo.setId(ESTADO_ACTIVO_ID);

                Estado inactivo = new Estado();
                inactivo.setId(ESTADO_INACTIVO_ID);

                User user = buildUser(300L);
                user.setPreferredEmpresa(buildEmpresa(11L));
                user.setPreferredRol(buildRol(101L));

                UsuarioRol expirado = new UsuarioRol();
                expirado.setId(30L);
                expirado.setEstado(activo);
                expirado.setFinalizaContratoEn(OffsetDateTime.now().minusDays(1));
                expirado.setUser(user);
                expirado.setEmpresa(buildEmpresa(11L));
                expirado.setRol(buildRol(101L));

                UsuarioRol alternativaActiva = new UsuarioRol();
                alternativaActiva.setId(31L);
                alternativaActiva.setUser(user);
                alternativaActiva.setEmpresa(buildEmpresa(12L));
                alternativaActiva.setRol(buildRol(102L));
                alternativaActiva.setEstado(activo);

                when(usuarioRolRepository.findByEstadoInactivoYFechaActivacion(eq(ESTADO_INACTIVO_ID), any()))
                                .thenReturn(List.of());
                when(usuarioRolRepository.findByEstadoActivoYFechaFinalizacionPasada(eq(ESTADO_ACTIVO_ID), any()))
                                .thenReturn(List.of(expirado));
                when(estadoRepository.findById(requireNonNull(ESTADO_INACTIVO_ID))).thenReturn(Optional.of(inactivo));
                when(usuarioRolRepository.findActivasByUserId(ESTADO_ACTIVO_ID, user.getId()))
                                .thenReturn(List.of(alternativaActiva));

                usuarioRolContratoService.procesarContratos();

                assertThat(user.getPreferredEmpresa().getId()).isEqualTo(12L);
                assertThat(user.getPreferredRol().getId()).isEqualTo(102L);
                verify(userRepository).save(user);
        }

        @Test
        void procesarContratos_clearsPreferredAssignment_whenExpiredWasPreferredAndNoAlternative() {
                Estado activo = new Estado();
                activo.setId(ESTADO_ACTIVO_ID);

                Estado inactivo = new Estado();
                inactivo.setId(ESTADO_INACTIVO_ID);

                User user = buildUser(400L);
                user.setPreferredEmpresa(buildEmpresa(21L));
                user.setPreferredRol(buildRol(201L));

                UsuarioRol expirado = new UsuarioRol();
                expirado.setId(40L);
                expirado.setEstado(activo);
                expirado.setFinalizaContratoEn(OffsetDateTime.now().minusDays(1));
                expirado.setUser(user);
                expirado.setEmpresa(buildEmpresa(21L));
                expirado.setRol(buildRol(201L));

                when(usuarioRolRepository.findByEstadoInactivoYFechaActivacion(eq(ESTADO_INACTIVO_ID), any()))
                                .thenReturn(List.of());
                when(usuarioRolRepository.findByEstadoActivoYFechaFinalizacionPasada(eq(ESTADO_ACTIVO_ID), any()))
                                .thenReturn(List.of(expirado));
                when(estadoRepository.findById(requireNonNull(ESTADO_INACTIVO_ID))).thenReturn(Optional.of(inactivo));
                when(usuarioRolRepository.findActivasByUserId(ESTADO_ACTIVO_ID, user.getId()))
                                .thenReturn(List.of());

                usuarioRolContratoService.procesarContratos();

                assertThat(user.getPreferredEmpresa()).isNull();
                assertThat(user.getPreferredRol()).isNull();
                verify(userRepository).save(user);
        }

        @Test
        void procesarContratos_doesNothingWhenNoAssignmentsNeedChanges() {
                when(usuarioRolRepository.findByEstadoInactivoYFechaActivacion(eq(ESTADO_INACTIVO_ID), any()))
                                .thenReturn(List.of());
                when(usuarioRolRepository.findByEstadoActivoYFechaFinalizacionPasada(eq(ESTADO_ACTIVO_ID), any()))
                                .thenReturn(List.of());

                usuarioRolContratoService.procesarContratos();

                verify(usuarioRolRepository).findByEstadoInactivoYFechaActivacion(eq(ESTADO_INACTIVO_ID), any());
                verify(usuarioRolRepository).findByEstadoActivoYFechaFinalizacionPasada(eq(ESTADO_ACTIVO_ID), any());
                verifyNoMoreInteractions(usuarioRolRepository);
                verifyNoInteractions(estadoRepository);
                verifyNoInteractions(userRepository);
        }

        private User buildUser(Long id) {
                User user = new User();
                user.setId(id);
                return user;
        }

        private Empresa buildEmpresa(Long id) {
                Empresa empresa = new Empresa();
                empresa.setId(id);
                return empresa;
        }

        private Rol buildRol(Long id) {
                Rol rol = new Rol();
                rol.setId(id);
                return rol;
        }

}