package com.inventario.validator.seguridad;

import com.inventario.empresarol.EmpresaRol;
import com.inventario.empresarol.repositories.EmpresaRolRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.validator.common.BaseValidator;
import com.inventario.validator.parametrizacion.constantes.EstadoConstantes;
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
