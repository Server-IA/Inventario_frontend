package com.inventario.validator.inventario.entidades;

import com.inventario.criterioEvaluacion.CriterioEvaluacion;
import com.inventario.criterioEvaluacion.repositirories.CriterioEvaluacionRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriterioEvaluacionValidator implements BaseValidator {

    private final CriterioEvaluacionRepository criterioEvaluacionRepository;


    public CriterioEvaluacion validarCriterioEvaluacionPorEmpresa(Long criterioEvId, Long empresaId){
        return criterioEvaluacionRepository.findByIdAndEmpresaId(criterioEvId, empresaId)
                .orElseThrow(()-> new NotFoundException("criterio-evaluacion.not-found", criterioEvId));
    }
}
