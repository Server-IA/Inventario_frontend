package com.inventario.empresarol.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventario.auditoria.AuthenticationService;
import com.inventario.empresa.Empresa;
import com.inventario.empresarol.EmpresaRol;
import com.inventario.empresarol.dtos.requests.EmpresaRolCreateRequestDTO;
import com.inventario.empresarol.dtos.requests.EmpresaRolUpdateRequestDTO;
import com.inventario.empresarol.dtos.responses.EmpresaRolResponseDTO;
import com.inventario.empresarol.mappers.EmpresaRolMapper;
import com.inventario.empresarol.repositories.EmpresaRolRepository;
import com.inventario.estado.Estado;
import com.inventario.exceptionHandler.UserRoleForbiddenException;
import com.inventario.rol.Rol;
import com.inventario.user.User;
import com.inventario.utils.UserEmpresaService;
import com.inventario.validator.EntidadValidatorFacade;
import com.inventario.validator.parametrizacion.constantes.EstadoConstantes;
import com.inventario.validator.parametrizacion.constantes.RolConstantes;

@ExtendWith(MockitoExtension.class)
class EmpresaRolServiceTest {

    @Mock
    private EmpresaRolRepository empresaRolRepository;

    @Mock
    private EmpresaRolMapper empresaRolMapper;

    @Mock
    private UserEmpresaService userEmpresaService;

    @Mock
    private EntidadValidatorFacade entidadValidatorFacade;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private EmpresaRolService empresaRolService;

    @Test
    void findAll_returnsMappedRolesForCurrentEmpresa() {
        Long empresaId = 5L;
        EmpresaRol entity = new EmpresaRol();
        entity.setId(10L);

        EmpresaRolResponseDTO dto = buildResponseDto(10L, empresaId, "Empresa A", "Operario", "Activo");

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(empresaRolRepository.findByEmpresaId(empresaId)).thenReturn(List.of(entity));
        when(empresaRolMapper.toResponseDto(entity)).thenReturn(dto);

        List<EmpresaRolResponseDTO> result = empresaRolService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRolNombre()).isEqualTo("Operario");
    }

    @Test
    void create_persistsEmpresaRolWithAuditAndActiveEstado() {
        Long empresaId = 7L;
        Long rolId = 3L;

        Empresa empresa = new Empresa();
        empresa.setId(empresaId);

        Rol rol = Rol.builder().id(rolId).nombre("Operario").build();
        Estado estadoActivo = new Estado();
        estadoActivo.setId(EstadoConstantes.ESTADO_GENERAL_ACTIVO);

        User user = new User();
        user.setUsername("admin.empresa@inmero.co");

        EmpresaRol saved = new EmpresaRol();
        saved.setId(100L);

        EmpresaRolResponseDTO expected = buildResponseDto(100L, empresaId, "Empresa A", "Operario", "Activo");

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarEmpresa(empresaId)).thenReturn(empresa);
        when(entidadValidatorFacade.validarRol(rolId)).thenReturn(rol);
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO)).thenReturn(estadoActivo);
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(empresaRolRepository.save(org.mockito.ArgumentMatchers.any(EmpresaRol.class))).thenReturn(saved);
        when(empresaRolMapper.toResponseDto(saved)).thenReturn(expected);

        EmpresaRolCreateRequestDTO request = new EmpresaRolCreateRequestDTO(rolId);
        EmpresaRolResponseDTO result = empresaRolService.create(request);

        assertThat(result.getId()).isEqualTo(100L);

        ArgumentCaptor<EmpresaRol> captor = ArgumentCaptor.forClass(EmpresaRol.class);
        verify(empresaRolRepository).save(captor.capture());
        EmpresaRol persisted = captor.getValue();

        assertThat(persisted.getEmpresa().getId()).isEqualTo(empresaId);
        assertThat(persisted.getRol().getId()).isEqualTo(rolId);
        assertThat(persisted.getEstado().getId()).isEqualTo(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        assertThat(persisted.getCreatedBy()).isEqualTo(user);
    }

    @Test
    void toggleEstadoEmpresaRol_changesActiveToInactive() {
        Long empresaRolId = 50L;
        Long empresaId = 9L;

        Estado activo = new Estado();
        activo.setId(EstadoConstantes.ESTADO_GENERAL_ACTIVO);

        Estado inactivo = new Estado();
        inactivo.setId(EstadoConstantes.ESTADO_GENERAL_INACTIVO);

        EmpresaRol empresaRol = new EmpresaRol();
        empresaRol.setId(empresaRolId);
        empresaRol.setEstado(activo);

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarEmpresaRol(empresaRolId, empresaId)).thenReturn(empresaRol);
        when(entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_INACTIVO)).thenReturn(inactivo);

        empresaRolService.toggleEstadoEmpresaRol(empresaRolId);

        assertThat(empresaRol.getEstado().getId()).isEqualTo(EstadoConstantes.ESTADO_GENERAL_INACTIVO);
    }

    @Test
    void create_throwsUserRoleForbiddenException_whenRolIsAdministradorSistema() {
        Long empresaId = 7L;
        EmpresaRolCreateRequestDTO request = new EmpresaRolCreateRequestDTO(RolConstantes.ROLE_ADMINISTRADOR_SISTEMA);
        Empresa empresa = new Empresa();
        empresa.setId(empresaId);

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarEmpresa(empresaId)).thenReturn(empresa);

        assertThrows(UserRoleForbiddenException.class, () -> empresaRolService.create(request));

        verify(entidadValidatorFacade).validarEmpresa(empresaId);
        verifyNoInteractions(authenticationService, empresaRolMapper);
        verifyNoMoreInteractions(entidadValidatorFacade);
    }

    @Test
    void update_throwsUserRoleForbiddenException_whenRolIsAdministradorSistema() {
        Long empresaId = 7L;
        Long empresaRolId = 15L;

        EmpresaRol empresaRol = new EmpresaRol();
        empresaRol.setId(empresaRolId);

        User user = new User();
        user.setUsername("admin.empresa@inmero.co");

        EmpresaRolUpdateRequestDTO request = new EmpresaRolUpdateRequestDTO(RolConstantes.ROLE_ADMINISTRADOR_SISTEMA, null);

        when(userEmpresaService.getEmpresaIdFromCurrentRequest()).thenReturn(empresaId);
        when(entidadValidatorFacade.validarEmpresaRol(empresaRolId, empresaId)).thenReturn(empresaRol);
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        assertThrows(UserRoleForbiddenException.class, () -> empresaRolService.update(empresaRolId, request));

        verify(entidadValidatorFacade).validarEmpresaRol(empresaRolId, empresaId);
        verify(entidadValidatorFacade).validarRol(RolConstantes.ROLE_ADMINISTRADOR_SISTEMA);
        verify(authenticationService).getAuthenticatedUser();
        verifyNoMoreInteractions(entidadValidatorFacade);
    }

    private EmpresaRolResponseDTO buildResponseDto(Long id, Long empresaId, String empresaNombre, String rolNombre,
            String estadoNombre) {
        EmpresaRolResponseDTO dto = new EmpresaRolResponseDTO();
        dto.setId(id);
        dto.setEmpresaId(empresaId);
        dto.setEmpresaNombre(empresaNombre);
        dto.setRolNombre(rolNombre);
        dto.setEstadoNombre(estadoNombre);
        return dto;
    }
}
