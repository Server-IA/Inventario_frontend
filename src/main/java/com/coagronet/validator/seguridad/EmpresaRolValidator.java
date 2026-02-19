package com.coagronet.validator.seguridad;

import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.validator.common.BaseValidator;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmpresaRolValidator implements BaseValidator {

    private final EmpresaRolRepository empresaRolRepository;

    public EmpresaRol validarEmpresaRol(Long empresaRolId, Long empresaId){
        return empresaRolRepository.findByIdAndEmpresaId(empresaRolId, empresaId)
                .orElseThrow(()-> new NotFoundException("La empresa-rol no fue encontrada para esta empresa."));
    }

    public EmpresaRol validarEmpresaRolAdmin(Long empresaRolId){
        return empresaRolRepository.findById(empresaRolId)
                .orElseThrow(()-> new NotFoundException("La empresa-rol no fue encontrada."));
    }

    public EmpresaRol validarRolDeEmpresaActivo(Long empresaId, Long rolId){
        return empresaRolRepository.findByEmpresaIdAndRolIdAndEstadoId(empresaId, rolId, EstadoConstantes.ESTADO_GENERAL_ACTIVO)
                .orElseThrow(() -> new RuntimeException("El rol para esa empresa no fue encontrado o no esta activo"));
    }
}
