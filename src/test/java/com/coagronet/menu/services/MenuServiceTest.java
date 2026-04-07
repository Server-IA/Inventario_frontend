package com.coagronet.menu.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.infrastructure.security.JwtService;
import com.coagronet.menu.dtos.MenuModuloResponseDTO;
import com.coagronet.menu.dtos.MenuSubSistemaResponseDTO;
import com.coagronet.menu.repositories.MenuModuloRepository;
import com.coagronet.menu.repositories.projections.SubModuloRow;
import com.coagronet.modulo.Modulo;
import com.coagronet.modulo.mappers.ModuloMapper;
import com.coagronet.modulo.repositories.ModuloRepository;
import com.coagronet.moduloempresa.ModuloEmpresa;
import com.coagronet.moduloempresa.repositories.ModuloEmpresaRepository;
import com.coagronet.rol.Rol;
import com.coagronet.rol.repositories.RolRepository;
import com.coagronet.subsistema.SubSistema;
import com.coagronet.utils.UserEmpresaService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuModuloRepository menuModuloRepository;

    @Mock
    private ModuloMapper moduloMapper;

    @Mock
    private UserEmpresaService userEmpresaService;

    @Mock
    private ModuloRepository moduloRepository;

    @Mock
    private ModuloEmpresaRepository moduloEmpresaRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MenuService menuService;

    @Test
    void obtenerMenuPorEmpresaTipoYRol_returnsGroupedMenu_whenDataIsValid() {
        Long empresaId = 10L;
        SubModuloRow row1 = row("Inventario", "box", "kardex", "Kardex", "/kardex", "icon-kardex");
        SubModuloRow row2 = row("Inventario", "box", "producto", "Producto", "/producto", "icon-producto");

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(request.getHeader("Authorization")).thenReturn("Bearer token-123");
        when(jwtService.extractRoleId("token-123")).thenReturn(2);
        Rol rolAdminEmpresa = new Rol();
        rolAdminEmpresa.setId(2L);
        rolAdminEmpresa.setNombre("ADMINISTRADOR_EMPRESA");
        when(rolRepository.findById(2L)).thenReturn(java.util.Optional.of(rolAdminEmpresa));
        when(menuModuloRepository.findSubmodulosByEmpresaTipoAppAndRolId(empresaId, 1, 2, true)).thenReturn(List.of(row1, row2));
        when(moduloMapper.toDTO(row1)).thenReturn(new MenuModuloResponseDTO("kardex", "Kardex", "/kardex", "icon-kardex"));
        when(moduloMapper.toDTO(row2)).thenReturn(new MenuModuloResponseDTO("producto", "Producto", "/producto", "icon-producto"));

        List<MenuSubSistemaResponseDTO> result = menuService.obtenerMenuPorEmpresaTipoYRol("web");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Inventario");
        assertThat(result.get(0).modulos()).hasSize(2);
    }

    @Test
    void obtenerMenuPorEmpresaTipoYRol_doesNotFilterAdminEmpresa_whenRoleIsAdminSistema() {
        Long empresaId = 10L;

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(request.getHeader("Authorization")).thenReturn("Bearer token-123");
        when(jwtService.extractRoleId("token-123")).thenReturn(1);
        Rol rolAdminSistema = new Rol();
        rolAdminSistema.setId(1L);
        rolAdminSistema.setNombre("ADMINISTRADOR_SISTEMA");
        when(rolRepository.findById(1L)).thenReturn(java.util.Optional.of(rolAdminSistema));
        when(menuModuloRepository.findSubmodulosByEmpresaTipoAppAndRolId(empresaId, 1, 1, false)).thenReturn(List.of());

        List<MenuSubSistemaResponseDTO> result = menuService.obtenerMenuPorEmpresaTipoYRol("web");

        assertThat(result).isEmpty();
        verify(menuModuloRepository).findSubmodulosByEmpresaTipoAppAndRolId(empresaId, 1, 1, false);
    }

    @Test
    void obtenerMenuPorEmpresaTipoYRol_throwsBadRequest_whenTipoAplicacionIsInvalid() {
        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(10L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> menuService.obtenerMenuPorEmpresaTipoYRol("desktop"));

        assertThat(exception.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void obtenerMenuPorEmpresaTipoYRol_throwsAccessDenied_whenAuthorizationHeaderIsMissing() {
        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(10L);
        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> menuService.obtenerMenuPorEmpresaTipoYRol("web"));

        verifyNoInteractions(menuModuloRepository);
    }

    @Test
    void obtenerMenuPorEmpresaTipoYRol_throwsAccessDenied_whenRoleCannotBeExtracted() {
        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(10L);
        when(request.getHeader("Authorization")).thenReturn("Bearer token-123");
        when(jwtService.extractRoleId("token-123")).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> menuService.obtenerMenuPorEmpresaTipoYRol("web"));

        verifyNoInteractions(menuModuloRepository);
    }

    @Test
    void obtenerModulosDisponiblesParaEmpresa_groupsBySubSistema() {
        Long empresaId = 88L;
        SubSistema subsistema = new SubSistema();
        subsistema.setNombre("Inventario");
        subsistema.setIcon("box");

        Modulo modulo = new Modulo();
        modulo.setNombreId("kardex");
        modulo.setNombre("Kardex");
        modulo.setUrl("/kardex");
        modulo.setIcon("icon-kardex");
        modulo.setSubSistema(subsistema);

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(menuModuloRepository.findModulosNoAsignados(empresaId)).thenReturn(List.of(modulo));

        List<MenuSubSistemaResponseDTO> result = menuService.obtenerModulosDisponiblesParaEmpresa();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Inventario");
        assertThat(result.get(0).modulos()).hasSize(1);
        assertThat(result.get(0).modulos().get(0).id()).isEqualTo("kardex");
    }

    @Test
    void asignarModulosAEmpresa_savesOnlyNonExistingAssignments() {
        Long empresaId = 11L;
        Empresa empresa = new Empresa();
        empresa.setId(empresaId);

        Estado estadoActivo = new Estado();
        estadoActivo.setId(1L);

        Modulo modulo1 = new Modulo();
        modulo1.setId(101L);
        modulo1.setNombreId("kardex");

        Modulo modulo2 = new Modulo();
        modulo2.setId(102L);
        modulo2.setNombreId("producto");

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(empresaRepository.findById(empresaId)).thenReturn(java.util.Optional.of(empresa));
        when(moduloRepository.findByNombreIdIn(List.of("kardex", "producto"))).thenReturn(List.of(modulo1, modulo2));
        when(estadoRepository.findById(1L)).thenReturn(java.util.Optional.of(estadoActivo));
        when(moduloEmpresaRepository.existsByEmpresaAndModulo(empresa, modulo1)).thenReturn(false);
        when(moduloEmpresaRepository.existsByEmpresaAndModulo(empresa, modulo2)).thenReturn(true);

        menuService.asignarModulosAEmpresa(List.of("kardex", "producto"));

        ArgumentCaptor<List<ModuloEmpresa>> captor = ArgumentCaptor.forClass(List.class);
        verify(moduloEmpresaRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getModulo().getNombreId()).isEqualTo("kardex");
    }

    @Test
    void asignarModulosAEmpresa_throwsRuntimeException_whenNoValidModulesAreFound() {
        Long empresaId = 11L;
        Empresa empresa = new Empresa();
        empresa.setId(empresaId);

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(empresaRepository.findById(empresaId)).thenReturn(java.util.Optional.of(empresa));
        when(moduloRepository.findByNombreIdIn(List.of("invalido"))).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> menuService.asignarModulosAEmpresa(List.of("invalido")));
    }

    private SubModuloRow row(String subNombre, String subIcon, String modId, String modNombre, String modUrl,
            String modIcon) {
        return new SubModuloRow() {
            @Override
            public String getSubNombre() {
                return subNombre;
            }

            @Override
            public String getSubIcon() {
                return subIcon;
            }

            @Override
            public String getModNombreId() {
                return modId;
            }

            @Override
            public String getModNombre() {
                return modNombre;
            }

            @Override
            public String getModUrl() {
                return modUrl;
            }

            @Override
            public String getModIcon() {
                return modIcon;
            }
        };
    }
}
