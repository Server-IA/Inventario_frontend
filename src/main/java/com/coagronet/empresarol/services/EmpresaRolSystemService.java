/*=============================================================================
 Nombre del archivo : EmpresaRolSystemService.java
 Descripcion        : Servicio del sistema para la gestión interna de roles de empresa.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-24 | 0.4.0   | JUAN JOSE CASTRO     | Reemplazo del uso de        |
 |            |         |                      | OffsetDateTime por Instant  |
 |            |         |                      | para establecer las fechas  |
 |            |         |                      | de auditoría. Cambio en la  |
 |            |         |                      | asignación de createdBy y   |
 |            |         |                      | updatedBy para usar la      |
 |            |         |                      | entidad User.               |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.empresarol.services;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.empresa.Empresa;
import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.empresarol.dtos.requests.EmpresaRolSystemCreateRequestDTO;
import com.coagronet.empresarol.dtos.requests.EmpresaRolSystemUpdateRequestDTO;
import com.coagronet.empresarol.dtos.responses.EmpresaRolResponseDTO;
import com.coagronet.empresarol.mappers.EmpresaRolMapper;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.rol.Rol;
import com.coagronet.user.User;
import com.coagronet.validator.EntidadValidatorFacade;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaRolSystemService {
    private final EmpresaRolRepository empresaRolRepository;
    private final EmpresaRolMapper empresaRolMapper;
    private final EntidadValidatorFacade entidadValidatorFacade;
    private final AuthenticationService authenticationService;

    public List<EmpresaRolResponseDTO> findAll() {
        return empresaRolRepository.findAll().stream().map(empresaRolMapper::toResponseDto).toList();
    }

    public EmpresaRolResponseDTO findById(Long id) {
        return empresaRolRepository.findById(id).map(empresaRolMapper::toResponseDto)
                .orElseThrow(() -> new NotFoundException("empresa-rol.not-found"));
    }

    @Transactional
    public EmpresaRolResponseDTO create(EmpresaRolSystemCreateRequestDTO dto) {
        Empresa empresa = entidadValidatorFacade.validarEmpresa(dto.getEmpresaId());
        Rol rol = entidadValidatorFacade.validarRol(dto.getRolId());
        Estado estado = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);

        User user = authenticationService.getAuthenticatedUser();

        EmpresaRol empresaRol = EmpresaRol.builder()
                .empresa(empresa)
                .rol(rol)
                .estado(estado)
                .createdBy(user)
                .build();

        empresaRol = empresaRolRepository.save(empresaRol);
        return empresaRolMapper.toResponseDto(empresaRol);

    }

    @Transactional
    public void update(Long id, EmpresaRolSystemUpdateRequestDTO dto) {

        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRolAdmin(id);
        User user = authenticationService.getAuthenticatedUser();

        if (dto.getRolId() != null) {
            Rol rol = entidadValidatorFacade.validarRol(dto.getRolId());
            empresaRol.setRol(rol);
        }

        if (dto.getEstadoId() != null) {
            Estado estado = entidadValidatorFacade.validarEstadoGeneral(dto.getEstadoId());
            empresaRol.setEstado(estado);
        }

        empresaRolMapper.updateAdminEntityFromDto(dto, empresaRol);
        empresaRol.setUpdatedBy(user);
        empresaRol.setUpdatedAt(Instant.now());
    }

    @Transactional
    public void updateEstado(Long id, Long estadoId) {
        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRolAdmin(id);
        Estado estado = entidadValidatorFacade.validarEstadoGeneral(estadoId);
        User user = authenticationService.getAuthenticatedUser();

        empresaRol.setEstado(estado);
        empresaRol.setUpdatedBy(user);
        empresaRol.setUpdatedAt(Instant.now());
    }

    @Transactional
    public void toggleEstadoEmpresaRol(Long id) {
        Estado nuevoEstado;
        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRolAdmin(id);
        if (empresaRol.getEstado().getId().equals(EstadoConstantes.ESTADO_GENERAL_ACTIVO)) {
            nuevoEstado = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_INACTIVO);
        } else {
            nuevoEstado = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        }
        empresaRol.setEstado(nuevoEstado);
    }

    @Transactional
    public void delete(Long id) {

        entidadValidatorFacade.validarEmpresaRolAdmin(id);
        empresaRolRepository.deleteById(id);
    }

}
