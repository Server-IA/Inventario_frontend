package com.coagronet.empresarol.services;

import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.empresa.Empresa;
import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.empresarol.dtos.requests.EmpresaRolCreateRequestDTO;
import com.coagronet.empresarol.dtos.requests.EmpresaRolUpdateRequestDTO;
import com.coagronet.empresarol.dtos.responses.EmpresaRolResponseDTO;
import com.coagronet.empresarol.mappers.EmpresaRolMapper;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.UserRoleForbiddenException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.rol.Rol;
import com.coagronet.user.User;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;
import com.coagronet.validator.parametrizacion.constantes.RolConstantes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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

        User currentUser = authenticationService.getAuthenticatedUser();

        EmpresaRol empresaRol = EmpresaRol.builder()
                .empresa(empresa)
                .rol(rol)
                .createdBy(currentUser)
                .estado(estado)
                .build();

        empresaRol = empresaRolRepository.save(empresaRol);
        return empresaRolMapper.toResponseDto(empresaRol);
    }

    @Transactional
    public void update(Long id, EmpresaRolUpdateRequestDTO dto) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRol(id, empresaId);
        User currentUser = authenticationService.getAuthenticatedUser();

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
        empresaRol.setUpdatedBy(currentUser);
        empresaRol.setUpdatedAt(Instant.now());
    }

    @Transactional
    public void updateEstado(Long id, Long estadoId) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRol(id, empresaId);
        Estado estado = entidadValidatorFacade.validarEstadoGeneral(estadoId);
        User currentUser = authenticationService.getAuthenticatedUser();

        empresaRol.setEstado(estado);
        empresaRol.setUpdatedBy(currentUser);
        empresaRol.setUpdatedAt(Instant.now());
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
