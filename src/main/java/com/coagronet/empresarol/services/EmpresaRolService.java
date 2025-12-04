package com.coagronet.empresarol.services;

import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.empresa.Empresa;
import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.empresarol.dtos.EmpresaRolRequestDTO;
import com.coagronet.empresarol.dtos.EmpresaRolResponseDTO;
import com.coagronet.empresarol.mappers.EmpresaRolMapper;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.rol.Rol;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public EmpresaRolResponseDTO create(EmpresaRolRequestDTO dto){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        Empresa empresa = entidadValidatorFacade.validarEmpresa(empresaId);
        Rol rol = entidadValidatorFacade.validarRol(dto.getRolId());

        String username = authenticationService.getAuthenticatedUser().getUsername();

        EmpresaRol empresaRol = EmpresaRol.builder()
                .empresa(empresa)
                .rol(rol)
                .createdBy(username)
                .build();

        empresaRol = empresaRolRepository.save(empresaRol);
        return empresaRolMapper.toResponseDto(empresaRol);
    }

    public void update(Long id, EmpresaRolRequestDTO dto){
    }

}
