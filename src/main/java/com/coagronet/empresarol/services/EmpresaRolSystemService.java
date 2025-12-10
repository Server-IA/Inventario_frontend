package com.coagronet.empresarol.services;

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
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaRolSystemService {
    private final EmpresaRolRepository empresaRolRepository;
    private final EmpresaRolMapper empresaRolMapper;
    private final UserEmpresaService userEmpresaService;
    private final EntidadValidatorFacade entidadValidatorFacade;
    private final AuthenticationService authenticationService;


    public List<EmpresaRolResponseDTO> findAll(){
        return empresaRolRepository.findAll().stream().map(empresaRolMapper::toResponseDto).toList();
    }

    public EmpresaRolResponseDTO findById(Long id){
        return empresaRolRepository.findById(id).map(empresaRolMapper::toResponseDto)
                .orElseThrow(()-> new NotFoundException("empresa-rol.not-found"));
    }

    @Transactional
    public EmpresaRolResponseDTO create(EmpresaRolSystemCreateRequestDTO dto){
        Empresa empresa = entidadValidatorFacade.validarEmpresa(dto.getEmpresaId());
        Rol rol = entidadValidatorFacade.validarRol(dto.getRolId());
        Estado estado = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);

        String username = authenticationService.getAuthenticatedUser().getUsername();

        EmpresaRol empresaRol = EmpresaRol.builder()
                .empresa(empresa)
                .rol(rol)
                .estado(estado)
                .createdBy(username)
                .build();

        empresaRol = empresaRolRepository.save(empresaRol);
        return empresaRolMapper.toResponseDto(empresaRol);

    }

    @Transactional
    public void update(Long id, EmpresaRolSystemUpdateRequestDTO dto){

        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRolAdmin(id);
        String username = authenticationService.getAuthenticatedUser().getUsername();

        if (dto.getRolId() != null) {
            Rol rol = entidadValidatorFacade.validarRol(dto.getRolId());
            empresaRol.setRol(rol);
        }

        if (dto.getEstadoId() != null) {
            Estado estado = entidadValidatorFacade.validarEstadoGeneral(dto.getEstadoId());
            empresaRol.setEstado(estado);
        }

        empresaRolMapper.updateAdminEntityFromDto(dto, empresaRol);
        empresaRol.setUpdatedBy(username);
        empresaRol.setUpdatedAt(OffsetDateTime.now());
    }

    @Transactional
    public void updateEstado(Long id, Long estadoId) {
        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRolAdmin(id);
        Estado estado = entidadValidatorFacade.validarEstadoGeneral(estadoId);
        String username = authenticationService.getAuthenticatedUser().getUsername();


        empresaRol.setEstado(estado);
        empresaRol.setUpdatedBy(username);
        empresaRol.setUpdatedAt(OffsetDateTime.now());
    }
    @Transactional
    public void toggleEstadoEmpresaRol(Long id){
        Estado nuevoEstado;
        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRolAdmin(id);
        if(empresaRol.getEstado().getId().equals(EstadoConstantes.ESTADO_GENERAL_ACTIVO)){
            nuevoEstado = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_INACTIVO);
        }else {
            nuevoEstado = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        }
        empresaRol.setEstado(nuevoEstado);
    }

    @Transactional
    public void delete(Long id){

        entidadValidatorFacade.validarEmpresaRolAdmin(id);
        empresaRolRepository.deleteById(id);
    }


}
