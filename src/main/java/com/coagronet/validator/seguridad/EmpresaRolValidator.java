package com.coagronet.validator.seguridad;

import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.empresarol.repositories.EmpresaRolRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmpresaRolValidator implements BaseValidator {

    private final EmpresaRolRepository empresaRolRepository;

    public EmpresaRol validarEmpresaRol(Long empresaRolId, Long empresaId){
        return empresaRolRepository.findByIdAndEmpresaId(empresaRolId, empresaId)
                .orElseThrow(()-> new NotFoundException("La empresa-rol no fue encontrada en esta empresa."));
    }
}
