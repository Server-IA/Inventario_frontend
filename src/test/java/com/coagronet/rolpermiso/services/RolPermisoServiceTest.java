package com.coagronet.rolpermiso.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.estado.Estado;
import com.coagronet.metodo.Metodo;
import com.coagronet.metodo.repositories.MetodoRepository;
import com.coagronet.modulo.Modulo;
import com.coagronet.moduloempresa.repositories.ModuloEmpresaRepository;
import com.coagronet.permiso.Permiso;
import com.coagronet.permiso.repositories.PermisoRepository;
import com.coagronet.rol.Rol;
import com.coagronet.rolpermiso.RolPermiso;
import com.coagronet.rolpermiso.dtos.request.ModuloMetodoRequest;
import com.coagronet.rolpermiso.dtos.response.RolPermisoAsignadoResponse;
import com.coagronet.rolpermiso.repositories.RolPermisoRepository;
import com.coagronet.user.User;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;

@ExtendWith(MockitoExtension.class)
class RolPermisoServiceTest {

    @Mock
    private RolPermisoRepository rolPermisoRepository;

    @Mock
    private PermisoRepository permisoRepository;

    @Mock
    private EmpresaRolRepository empresaRolRepository;

    @Mock
    private MetodoRepository metodoRepository;

    @Mock
    private UserEmpresaService userEmpresaService;

    @Mock
    private EntidadValidatorFacade entidadValidatorFacade;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private ModuloEmpresaRepository moduloEmpresaRepository;

    @InjectMocks
    private RolPermisoService rolPermisoService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void asignarModulosPermisos_assignsAllPermisosForModules() {
        Long empresaId = 1L;
        Long rolId = 2L;

        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");
        Estado estadoActivo = buildEstado(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        User currentUser = buildUser(99L, "admin");

        Permiso permiso1 = buildPermiso(101L, "Permiso A", "AUT_A", "GET", "Inventario");
        Permiso permiso2 = buildPermiso(102L, "Permiso B", "AUT_B", "POST", "Inventario");

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(permisoRepository.findPermisosByModulosIdsAndAdminEmpresaTrue(List.of(10L))).thenReturn(List.of(permiso1, permiso2));
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(moduloEmpresaRepository.findModuloIdsByEmpresaIdAndModuloIdIn(org.mockito.ArgumentMatchers.eq(empresaId), any()))
                .thenReturn(Collections.emptySet());
        when(rolPermisoRepository.findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(101L, 102L)))
                .thenReturn(Collections.emptySet());

        RolPermisoAsignadoResponse response = rolPermisoService.asignarModulosPermisos(rolId, List.of(10L));

        assertThat(response.getRolId()).isEqualTo(rolId);
        assertThat(response.getPermisosAsignados()).isEqualTo(2);
        ArgumentCaptor<List<RolPermiso>> captor = ArgumentCaptor.forClass(List.class);
        verify(rolPermisoRepository, times(1)).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        verify(moduloEmpresaRepository, times(1)).saveAll(any());
    }

    @Test
    void asignarModulosPermisos_throwsRuntimeException_whenNoPermisosFound() {
        Long empresaId = 1L;
        Long rolId = 2L;

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(buildEmpresaRol(22L, rolId, "Operario"));
        when(permisoRepository.findPermisosByModulosIdsAndAdminEmpresaTrue(List.of(10L))).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> rolPermisoService.asignarModulosPermisos(rolId, List.of(10L)));
    }

    @Test
    void quitarModulosPermisos_deletesOnlyResolvedPermisos() {
        Long empresaId = 1L;
        Long rolId = 3L;

        EmpresaRol empresaRol = buildEmpresaRol(99L, rolId, "Operario");
        Permiso permisoA = buildPermiso(77L, "Permiso A", "AUT_A", "GET", "Inventario");
        Permiso permisoB = buildPermiso(88L, "Permiso B", "AUT_B", "POST", "Inventario");

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(permisoRepository.findPermisosByModulosIdsAndAdminEmpresaTrue(List.of(5L))).thenReturn(List.of(permisoA, permisoB));

        rolPermisoService.quitarModulosPermisos(rolId, List.of(5L));

        verify(rolPermisoRepository).deleteByEmpresaRolIdAndPermisoIds(99L, List.of(77L, 88L));
    }

    @Test
    void asignarModulosPermisosLectura_assignsOnlyReadPermissions() {
        Long empresaId = 1L;
        Long rolId = 2L;

        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");
        Estado estadoActivo = buildEstado(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        User currentUser = buildUser(99L, "admin");

        Permiso readByMethod = buildPermiso(1L, "Read A", "INV_READ", "GET", "Inventario");
        Permiso readByAuthority = buildPermiso(2L, "Read B", "INVENTARIO_READ_ALL", "POST", "Inventario");
        Permiso write = buildPermiso(3L, "Write", "INV_WRITE", "POST", "Inventario");

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(permisoRepository.findPermisosByModulosIdsAndAdminEmpresaTrue(List.of(10L))).thenReturn(List.of(readByMethod, readByAuthority, write));
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(moduloEmpresaRepository.findModuloIdsByEmpresaIdAndModuloIdIn(org.mockito.ArgumentMatchers.eq(empresaId), any()))
                .thenReturn(Collections.emptySet());
        when(rolPermisoRepository.findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(1L, 2L)))
                .thenReturn(Collections.emptySet());

        RolPermisoAsignadoResponse response = rolPermisoService.asignarModulosPermisosLectura(rolId, List.of(10L));

        assertThat(response.getPermisosAsignados()).isEqualTo(2);

        ArgumentCaptor<List<RolPermiso>> captor = ArgumentCaptor.forClass(List.class);
        verify(rolPermisoRepository, times(1)).saveAll(captor.capture());
        assertThat(captor.getValue()).extracting(rp -> rp.getPermiso().getId()).containsExactlyInAnyOrder(1L, 2L);
        verify(moduloEmpresaRepository, times(1)).saveAll(any());
    }

    @Test
    void asignarModulosPermisosConMetodos_assignsPermissionsMatchingMethodFilters() {
        Long empresaId = 1L;
        Long rolId = 2L;

        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");
        Estado estadoActivo = buildEstado(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        User currentUser = buildUser(99L, "admin");

        Permiso getPermiso = buildPermiso(10L, "Read", "INV_READ", "GET", "Inventario");
        Permiso postPermiso = buildPermiso(11L, "Create", "INV_CREATE", "POST", "Inventario");
        getPermiso.getModulo().setId(100L);
        postPermiso.getModulo().setId(100L);

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(permisoRepository.findPermisosByModulosIdsAndAdminEmpresaTrue(List.of(100L))).thenReturn(List.of(getPermiso, postPermiso));
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(moduloEmpresaRepository.findModuloIdsByEmpresaIdAndModuloIdIn(org.mockito.ArgumentMatchers.eq(empresaId), any()))
                .thenReturn(Collections.emptySet());
        when(rolPermisoRepository.findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(10L)))
                .thenReturn(Collections.emptySet());

        ModuloMetodoRequest request = ModuloMetodoRequest.builder()
                .moduloId(100L)
                .metodos(List.of("GET"))
                .build();

        RolPermisoAsignadoResponse response = rolPermisoService.asignarModulosPermisosConMetodos(rolId, List.of(request));

        assertThat(response.getPermisosAsignados()).isEqualTo(1);
        verify(rolPermisoRepository, times(1)).saveAll(any());
        verify(moduloEmpresaRepository, times(1)).saveAll(any());
    }

    @Test
    void asignarModulosPermisosConMetodos_reportsOnlyNewAssignments_whenSomeAlreadyAssigned() {
        Long empresaId = 1L;
        Long rolId = 2L;

        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");
        Estado estadoActivo = buildEstado(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        User currentUser = buildUser(99L, "admin");

        Permiso getPermiso = buildPermiso(10L, "Read", "INV_READ", "GET", "Inventario");
        Permiso postPermiso = buildPermiso(11L, "Create", "INV_CREATE", "POST", "Inventario");
        getPermiso.getModulo().setId(100L);
        postPermiso.getModulo().setId(100L);

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(permisoRepository.findPermisosByModulosIdsAndAdminEmpresaTrue(List.of(100L))).thenReturn(List.of(getPermiso, postPermiso));
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(moduloEmpresaRepository.findModuloIdsByEmpresaIdAndModuloIdIn(org.mockito.ArgumentMatchers.eq(empresaId), any()))
                .thenReturn(Collections.emptySet());
        when(rolPermisoRepository.findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(10L, 11L)))
                .thenReturn(Set.of(10L));

        ModuloMetodoRequest request = ModuloMetodoRequest.builder()
                .moduloId(100L)
                .metodos(List.of("ALL"))
                .build();

        RolPermisoAsignadoResponse response = rolPermisoService.asignarModulosPermisosConMetodos(rolId, List.of(request));

        assertThat(response.getPermisosAsignados()).isEqualTo(1);
    }

    @Test
    void getModulosBySubsistemas_usesAdminEmpresaFilteredQuery() {
        Permiso permiso = buildPermiso(201L, "Ver Kardex", "KARDEX_READ", "GET", "Kardex");

        when(permisoRepository.findPermisosActivosAdminEmpresaBySubsistemas(List.of(1L, 2L)))
            .thenReturn(List.of(permiso));

        var response = rolPermisoService.getModulosBySubsistemas(List.of(1L, 2L));

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getPermisos()).hasSize(1);
        verify(permisoRepository).findPermisosActivosAdminEmpresaBySubsistemas(List.of(1L, 2L));
    }

    @Test
    void asignarPermisosAEmpresaRol_filtersToAdminEmpresa_whenUserIsNotSystemAdmin() {
        Long empresaId = 1L;
        Long rolId = 2L;

        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");
        Estado estadoActivo = buildEstado(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        User currentUser = buildUser(99L, "admin-empresa");
        Permiso permisoAdminEmpresa = buildPermiso(301L, "Permiso Empresa", "EMPRESA_READ", "GET", "Inventario");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
            "admin-empresa",
            "N/A",
            List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR_EMPRESA"))));

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(permisoRepository.findByIdInAndAdminEmpresaTrue(List.of(301L, 999L)))
            .thenReturn(List.of(permisoAdminEmpresa));
        when(moduloEmpresaRepository.findModuloIdsByEmpresaIdAndModuloIdIn(org.mockito.ArgumentMatchers.eq(empresaId), any()))
                .thenReturn(Collections.emptySet());
        when(rolPermisoRepository.findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(301L)))
            .thenReturn(Collections.emptySet());

        rolPermisoService.asignarPermisosAEmpresaRol(rolId, List.of(301L, 999L));

        verify(permisoRepository).findByIdInAndAdminEmpresaTrue(List.of(301L, 999L));
        verify(permisoRepository, never()).findAllById(any());
        verify(rolPermisoRepository).findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(301L));
        verify(rolPermisoRepository).saveAll(any());
        verify(moduloEmpresaRepository, times(1)).saveAll(any());
    }

    @Test
    void asignarPermisosAEmpresaRol_filtersToAdminEmpresa_whenUserIsSystemAdmin() {
        Long empresaId = 1L;
        Long rolId = 2L;

        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");
        Estado estadoActivo = buildEstado(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        User currentUser = buildUser(100L, "admin-sistema");
        Permiso permisoSistema = buildPermiso(401L, "Permiso Sistema", "SISTEMA_WRITE", "POST", "Configuracion");
        Permiso permisoEmpresa = buildPermiso(402L, "Permiso Empresa", "EMPRESA_READ", "GET", "Inventario");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
            "admin-sistema",
            "N/A",
            Set.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR_SISTEMA"))));

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(permisoRepository.findByIdInAndAdminEmpresaTrue(List.of(401L, 402L)))
            .thenReturn(List.of(permisoEmpresa));
        when(moduloEmpresaRepository.findModuloIdsByEmpresaIdAndModuloIdIn(org.mockito.ArgumentMatchers.eq(empresaId), any()))
                .thenReturn(Collections.emptySet());
        when(rolPermisoRepository.findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(402L)))
            .thenReturn(Collections.emptySet());

        rolPermisoService.asignarPermisosAEmpresaRol(rolId, List.of(401L, 402L));

        verify(permisoRepository).findByIdInAndAdminEmpresaTrue(List.of(401L, 402L));
        verify(permisoRepository, never()).findAllById(any());
        verify(rolPermisoRepository).findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(402L));
        verify(rolPermisoRepository).saveAll(any());
        verify(moduloEmpresaRepository, times(1)).saveAll(any());

    }

    @Test
    void asignarPermisosAEmpresaRol_doesNotCreateModuloEmpresa_whenRelationAlreadyExists() {
        Long empresaId = 1L;
        Long rolId = 2L;

        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");
        Estado estadoActivo = buildEstado(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        User currentUser = buildUser(99L, "admin-empresa");
        Permiso permisoAdminEmpresa = buildPermiso(301L, "Permiso Empresa", "EMPRESA_READ", "GET", "Inventario");
        permisoAdminEmpresa.getModulo().setId(100L);

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(permisoRepository.findByIdInAndAdminEmpresaTrue(List.of(301L)))
                .thenReturn(List.of(permisoAdminEmpresa));
        when(moduloEmpresaRepository.findModuloIdsByEmpresaIdAndModuloIdIn(org.mockito.ArgumentMatchers.eq(empresaId), any()))
                .thenReturn(Set.of(100L));
        when(rolPermisoRepository.findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(301L)))
                .thenReturn(Collections.emptySet());

        rolPermisoService.asignarPermisosAEmpresaRol(rolId, List.of(301L));

        verify(moduloEmpresaRepository, never()).saveAll(any());
    }

    @Test
    void asignarPermisosAEmpresaRolWithEmpresaId_throwsIllegalArgumentException_whenEmpresaIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> rolPermisoService.asignarPermisosAEmpresaRolWithEmpresaId(2L, List.of(301L), null));
    }

    @Test
    void getPermisosByEmpresaRol_withEmpresaId_usesProvidedEmpresaId() {
        Long empresaId = 15L;
        Long rolId = 2L;
        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");
        Permiso permiso = buildPermiso(601L, "Ver", "EMPRESA_READ", "GET", "Inventario");

        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(rolPermisoRepository.findPermisosByEmpresaRolId(22L)).thenReturn(List.of(permiso));

        var result = rolPermisoService.getPermisosByEmpresaRol(rolId, empresaId);

        assertThat(result).hasSize(1);
        verify(entidadValidatorFacade).validarRolDeEmpresaActivo(empresaId, rolId);
    }

    @Test
    void getPermisosByEmpresaRol_withEmpresaId_throwsIllegalArgumentException_whenEmpresaIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> rolPermisoService.getPermisosByEmpresaRol(2L, null));
    }

    @Test
    void asignarPermisosAEmpresaRolWithEmpresaId_createsModuloEmpresaRelation_whenMissing() {
        Long empresaId = 9L;
        Long rolId = 2L;

        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");
        Estado estadoActivo = buildEstado(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        User currentUser = buildUser(101L, "admin-sistema");
        Permiso permiso = buildPermiso(500L, "Permiso Empresa", "EMPRESA_READ", "GET", "Inventario");
        permiso.getModulo().setId(900L);

        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(permisoRepository.findByIdInAndAdminEmpresaTrue(List.of(500L))).thenReturn(List.of(permiso));
        when(moduloEmpresaRepository.findModuloIdsByEmpresaIdAndModuloIdIn(org.mockito.ArgumentMatchers.eq(empresaId), any()))
                .thenReturn(Collections.emptySet());
        when(rolPermisoRepository.findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(500L)))
                .thenReturn(Collections.emptySet());

        rolPermisoService.asignarPermisosAEmpresaRolWithEmpresaId(rolId, List.of(500L), empresaId);

        verify(moduloEmpresaRepository, times(1)).saveAll(any());
        verify(rolPermisoRepository, times(1)).saveAll(any());
    }

    @Test
    void reemplazarPermisoDeEmpresaRol_createsModuloEmpresaRelation_whenMissing() {
        Long empresaId = 1L;
        Long rolId = 2L;
        Long permisoActualId = 11L;
        Long nuevoPermisoId = 12L;

        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");
        Estado estadoActivo = buildEstado(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        User currentUser = buildUser(201L, "admin-empresa");
        Permiso nuevoPermiso = buildPermiso(nuevoPermisoId, "Nuevo", "INV_WRITE", "POST", "Inventario");
        nuevoPermiso.getModulo().setId(910L);

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(22L, permisoActualId)).thenReturn(true);
        when(rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(22L, nuevoPermisoId)).thenReturn(false);
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(permisoRepository.findByIdInAndAdminEmpresaTrue(List.of(nuevoPermisoId))).thenReturn(List.of(nuevoPermiso));
        when(moduloEmpresaRepository.findModuloIdsByEmpresaIdAndModuloIdIn(org.mockito.ArgumentMatchers.eq(empresaId), any()))
                .thenReturn(Collections.emptySet());

        rolPermisoService.reemplazarPermisoDeEmpresaRol(rolId, permisoActualId, nuevoPermisoId);

        verify(moduloEmpresaRepository, times(1)).saveAll(any());
    }

    @Test
    void reemplazarModuloPermisosDeEmpresaRol_createsModuloEmpresaRelation_whenMissing() {
        Long empresaId = 1L;
        Long rolId = 2L;
        Long moduloActualId = 200L;
        Long nuevoModuloId = 300L;

        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");
        Estado estadoActivo = buildEstado(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        User currentUser = buildUser(202L, "admin-empresa");
        Permiso permisoActual = buildPermiso(701L, "Actual", "INV_READ", "GET", "Actual");
        Permiso permisoNuevo = buildPermiso(801L, "Nuevo", "INV_WRITE", "POST", "Nuevo");
        permisoNuevo.getModulo().setId(950L);

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(permisoRepository.findPermisosByModuloIdAndAdminEmpresaTrue(moduloActualId)).thenReturn(List.of(permisoActual));
        when(rolPermisoRepository.findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(701L)))
                .thenReturn(Set.of(701L));
        when(permisoRepository.findPermisosByModuloIdAndAdminEmpresaTrue(nuevoModuloId)).thenReturn(List.of(permisoNuevo));
        when(moduloEmpresaRepository.findModuloIdsByEmpresaIdAndModuloIdIn(org.mockito.ArgumentMatchers.eq(empresaId), any()))
                .thenReturn(Collections.emptySet());
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(rolPermisoRepository.findPermisoIdsByEmpresaRolIdAndPermisoIdIn(22L, List.of(801L)))
                .thenReturn(Collections.emptySet());

        rolPermisoService.reemplazarModuloPermisosDeEmpresaRol(rolId, moduloActualId, nuevoModuloId);

        verify(moduloEmpresaRepository, times(1)).saveAll(any());
    }

    @Test
    void quitarPermisosDeEmpresaRol_deletesPermisos_whenIdsProvided() {
        Long empresaId = 1L;
        Long rolId = 2L;
        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);

        rolPermisoService.quitarPermisosDeEmpresaRol(rolId, List.of(10L, 20L));

        verify(rolPermisoRepository).deleteByEmpresaRolIdAndPermisoIds(22L, List.of(10L, 20L));
    }

    @Test
    void quitarPermisosDeEmpresaRol_doesNothing_whenIdsAreEmpty() {
        Long empresaId = 1L;
        Long rolId = 2L;
        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);

        rolPermisoService.quitarPermisosDeEmpresaRol(rolId, Collections.emptyList());

        verify(rolPermisoRepository, never()).deleteByEmpresaRolIdAndPermisoIds(any(), any());
    }

    @Test
    void quitarPermisosDeEmpresaRol_withEmpresaId_deletesPermisos_whenIdsProvided() {
        Long empresaId = 15L;
        Long rolId = 2L;
        EmpresaRol empresaRol = buildEmpresaRol(22L, rolId, "Operario");

        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);

        rolPermisoService.quitarPermisosDeEmpresaRol(rolId, List.of(10L, 20L), empresaId);

        verify(rolPermisoRepository).deleteByEmpresaRolIdAndPermisoIds(22L, List.of(10L, 20L));
    }

    @Test
    void quitarPermisosDeEmpresaRol_withEmpresaId_throwsIllegalArgumentException_whenEmpresaIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> rolPermisoService.quitarPermisosDeEmpresaRol(2L, List.of(10L), null));
    }

    private EmpresaRol buildEmpresaRol(Long empresaRolId, Long rolId, String rolNombre) {
        Rol rol = Rol.builder().id(rolId).nombre(rolNombre).build();
        EmpresaRol empresaRol = new EmpresaRol();
        empresaRol.setId(empresaRolId);
        empresaRol.setRol(rol);
        return empresaRol;
    }

    private Permiso buildPermiso(Long id, String nombre, String autoridad, String metodoNombre, String moduloNombre) {
        Metodo metodo = new Metodo();
        metodo.setNombre(metodoNombre);

        Modulo modulo = new Modulo();
        modulo.setId(id + 1000);
        modulo.setNombre(moduloNombre);

        Estado estado = new Estado();
        estado.setId(1L);

        Permiso permiso = new Permiso();
        permiso.setId(id);
        permiso.setNombre(nombre);
        permiso.setAutoridad(autoridad);
        permiso.setMetodo(metodo);
        permiso.setModulo(modulo);
        permiso.setEstado(estado);
        return permiso;
    }

    private Estado buildEstado(Long id) {
        Estado estado = new Estado();
        estado.setId(id);
        return estado;
    }

    private User buildUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }
}
