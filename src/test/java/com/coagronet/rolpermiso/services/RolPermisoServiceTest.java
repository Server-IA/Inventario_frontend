package com.coagronet.rolpermiso.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.estado.Estado;
import com.coagronet.metodo.Metodo;
import com.coagronet.metodo.repositories.MetodoRepository;
import com.coagronet.modulo.Modulo;
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

    @InjectMocks
    private RolPermisoService rolPermisoService;

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
        when(permisoRepository.findPermisosByModulosIds(List.of(10L))).thenReturn(List.of(permiso1, permiso2));
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(22L, 101L)).thenReturn(false);
        when(rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(22L, 102L)).thenReturn(false);

        RolPermisoAsignadoResponse response = rolPermisoService.asignarModulosPermisos(rolId, List.of(10L));

        assertThat(response.getRolId()).isEqualTo(rolId);
        assertThat(response.getPermisosAsignados()).isEqualTo(2);
        verify(rolPermisoRepository, times(2)).save(any(RolPermiso.class));
    }

    @Test
    void asignarModulosPermisos_throwsRuntimeException_whenNoPermisosFound() {
        Long empresaId = 1L;
        Long rolId = 2L;

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(buildEmpresaRol(22L, rolId, "Operario"));
        when(permisoRepository.findPermisosByModulosIds(List.of(10L))).thenReturn(List.of());

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
        when(permisoRepository.findPermisosByModulosIds(List.of(5L))).thenReturn(List.of(permisoA, permisoB));

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
        when(permisoRepository.findPermisosByModulosIds(List.of(10L))).thenReturn(List.of(readByMethod, readByAuthority, write));
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(22L, 1L)).thenReturn(false);
        when(rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(22L, 2L)).thenReturn(false);

        RolPermisoAsignadoResponse response = rolPermisoService.asignarModulosPermisosLectura(rolId, List.of(10L));

        assertThat(response.getPermisosAsignados()).isEqualTo(2);

        ArgumentCaptor<RolPermiso> captor = ArgumentCaptor.forClass(RolPermiso.class);
        verify(rolPermisoRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).extracting(rp -> rp.getPermiso().getId()).containsExactlyInAnyOrder(1L, 2L);
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

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarRolDeEmpresaActivo(empresaId, rolId)).thenReturn(empresaRol);
        when(permisoRepository.findPermisosByModuloId(100L)).thenReturn(List.of(getPermiso, postPermiso));
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(currentUser);
        when(rolPermisoRepository.existsByEmpresaRolIdAndPermisoId(22L, 10L)).thenReturn(false);

        ModuloMetodoRequest request = ModuloMetodoRequest.builder()
                .moduloId(100L)
                .metodos(List.of("GET"))
                .build();

        RolPermisoAsignadoResponse response = rolPermisoService.asignarModulosPermisosConMetodos(rolId, List.of(request));

        assertThat(response.getPermisosAsignados()).isEqualTo(1);
        verify(rolPermisoRepository, times(1)).save(any(RolPermiso.class));
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
