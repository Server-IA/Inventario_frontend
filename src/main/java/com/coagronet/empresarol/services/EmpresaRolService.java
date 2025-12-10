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
public class EmpresaRolService {

    private final EmpresaRolRepository empresaRolRepository;
    private final EmpresaRolMapper empresaRolMapper;
    private final UserEmpresaService userEmpresaService;
    private final EntidadValidatorFacade entidadValidatorFacade;
    private final AuthenticationService authenticationService;


    public List<EmpresaRolResponseDTO>findAll(){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        return empresaRolRepository
                .findByEmpresaId(empresaId).stream().map(empresaRolMapper::toResponseDto).toList();
    }

    public EmpresaRolResponseDTO findById(Long id){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        return empresaRolMapper.toResponseDto(entidadValidatorFacade.validarEmpresaRol(id, empresaId));
    }

    @Transactional
    public EmpresaRolResponseDTO create(EmpresaRolCreateRequestDTO dto){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        Empresa empresa = entidadValidatorFacade.validarEmpresa(empresaId);
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
    public void update(Long id, EmpresaRolUpdateRequestDTO dto){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRol(id, empresaId);
        String username = authenticationService.getAuthenticatedUser().getUsername();

        if (dto.getRolId() != null) {
            Rol rol = entidadValidatorFacade.validarRol(dto.getRolId());
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
    public void toggleEstadoEmpresaRol(Long id){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        Estado nuevoEstado;
        EmpresaRol empresaRol = entidadValidatorFacade.validarEmpresaRol(id, empresaId);
        if(empresaRol.getEstado().getId().equals(EstadoConstantes.ESTADO_GENERAL_ACTIVO)){
            nuevoEstado = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_INACTIVO);
        }else {
            nuevoEstado = entidadValidatorFacade.validarEstadoGeneral(EstadoConstantes.ESTADO_GENERAL_ACTIVO);
        }
        empresaRol.setEstado(nuevoEstado);
    }

    @Transactional
    public void delete(Long id){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        entidadValidatorFacade.validarEmpresaRol(id, empresaId);

        empresaRolRepository.deleteById(id);
    }


}
