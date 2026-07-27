/*=============================================================================
 Nombre del archivo : EmpresaRolService.java
 Descripcion        : Servicio para la gestión de roles a nivel de empresa.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-27 | 0.4.0   | JUAN JOSE CASTRO     | Adición de funcionalidad    |
 |            |         |                      | para obtener la lista de    |
 |            |         |                      | roles activos para menús de |
 |            |         |                      | selección, ajustando la     |
 |            |         |                      | visibilidad de la           |
 |            |         |                      | información dependiendo del |
 |            |         |                      | nivel de acceso del usuario |
 |            |         |                      | en sesión.                  |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.empresarol.services;

import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.empresa.Empresa;
import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.empresarol.dtos.requests.EmpresaRolCreateRequestDTO;
import com.coagronet.empresarol.dtos.requests.EmpresaRolUpdateRequestDTO;
import com.coagronet.empresarol.dtos.responses.EmpresaRolResponseDTO;
import com.coagronet.empresarol.dtos.responses.EmpresaRolSelectDTO;
import com.coagronet.empresarol.mappers.EmpresaRolMapper;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.UserRoleForbiddenException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.rol.Rol;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;
import com.coagronet.validator.parametrizacion.constantes.RolConstantes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaRolService {

    private final EmpresaRolRepository empresaRolRepository;
    private final EmpresaRolMapper empresaRolMapper;
    private final UserEmpresaService userEmpresaService;
    private final EntidadValidatorFacade entidadValidatorFacade;
    private final AuthenticationService authenticationService;

    public List<EmpresaRolResponseDTO> findAll() {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        return empresaRolRepository
                .findByEmpresaId(empresaId).stream().map(empresaRolMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EmpresaRolSelectDTO> getForSelect() {
        boolean isSysAdmin = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            isSysAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR_SISTEMA"));
        }

        if (isSysAdmin) {
            return empresaRolRepository.findAll().stream()
                    .filter(er -> er.getRol().getDeletedAt() == null && er.getEstado().getId() == 1L)
                    .map(er -> new EmpresaRolSelectDTO(er.getId(),
                            er.getEmpresa().getNombre() + " - " + er.getRol().getNombre()))
                    .toList();
        } else {
            Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
            return empresaRolRepository.findByEmpresaId(empresaId).stream()
                    .filter(er -> er.getRol().getDeletedAt() == null && er.getEstado().getId() == 1L)
                    .map(er -> new EmpresaRolSelectDTO(er.getRol().getId(), er.getRol().getNombre()))
                    .toList();
        }
    }

    public EmpresaRolResponseDTO findById(Long id) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        return empresaRolMapper.toResponseDto(entidadValidatorFacade.validarEmpresaRol(id, empresaId));
    }

    @Transactional
    public EmpresaRolResponseDTO create(EmpresaRolCreateRequestDTO dto) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        Empresa empresa = entidadValidatorFacade.validarEmpresa(empresaId);
        if (RolConstantes.ROLE_ADMINISTRADOR_SISTEMA.equals(dto.getRolId())) {
            throw new UserRoleForbiddenException("No puedes asignar ese rol");
        }
        if (empresaRolRepository.existsByEmpresaIdAndRolId(empresaId, dto.getRolId())) {
            throw new BadRequestException("Ese rol ya está asignado a la empresa");
        }

        Rol rol = entidadValidatorFacade.validarRol(dto.getRolId());
        Estado estado = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);

        String username = authenticationService.getAuthenticatedUser().getUsername();

        EmpresaRol empresaRol = EmpresaRol.builder()
                .empresa(empresa)
                .rol(rol)
                .createdBy(username)
                .estado(estado)
                .build();

        empresaRol = empresaRolRepository.save(empresaRol);
        return empresaRolMapper.toResponseDto(empresaRol);
    }

    @Transactional
    public void update(Long id, EmpresaRolUpdateRequestDTO dto) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRol(id, empresaId);
        String username = authenticationService.getAuthenticatedUser().getUsername();

        if (dto.getRolId() != null) {
            Rol rol = entidadValidatorFacade.validarRol(dto.getRolId());

            if (RolConstantes.ROLE_ADMINISTRADOR_SISTEMA.equals(dto.getRolId())) {
                throw new UserRoleForbiddenException("No puedes asignar ese rol");
            }
            empresaRol.setRol(rol);
        }

        if (dto.getEstadoId() != null) {
            Estado estado = entidadValidatorFacade.validarEstadoGeneral(dto.getEstadoId());
            empresaRol.setEstado(estado);
        }

        empresaRolMapper.updateEntityFromDto(dto, empresaRol);
        empresaRol.setUpdatedBy(username);
        empresaRol.setUpdatedAt(OffsetDateTime.now());
    }

    @Transactional
    public void updateEstado(Long id, Long estadoId) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRol(id, empresaId);
        Estado estado = entidadValidatorFacade.validarEstadoGeneral(estadoId);
        String username = authenticationService.getAuthenticatedUser().getUsername();

        empresaRol.setEstado(estado);
        empresaRol.setUpdatedBy(username);
        empresaRol.setUpdatedAt(OffsetDateTime.now());
    }

    @Transactional
    public void toggleEstadoEmpresaRol(Long id) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        Estado nuevoEstado;
        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRol(id, empresaId);
        if (empresaRol.getEstado().getId().equals(EstadoConstantes.ESTADO_GENERAL_ACTIVO)) {
            nuevoEstado = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_INACTIVO);
        } else {
            nuevoEstado = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        }
        empresaRol.setEstado(nuevoEstado);
    }

    @Transactional
    public void delete(Long id) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        entidadValidatorFacade.validarEmpresaRol(id, empresaId);

        empresaRolRepository.deleteById(id);
    }

}
