package com.coagronet.validator.inventario.entidades;

import com.coagronet.criterioEvaluacion.CriterioEvaluacion;
import com.coagronet.criterioEvaluacion.repositirories.CriterioEvaluacionRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriterioEvaluacionValidator {

    private final CriterioEvaluacionRepository criterioEvaluacionRepository;


    public CriterioEvaluacion validarCriterioEvaluacionPorEmpresa(Long criterioEvId, Long empresaId){
        return criterioEvaluacionRepository.findByIdAndEmpresaId(criterioEvId, empresaId)
                .orElseThrow(()-> new NotFoundException("criterio-evaluacion.not-found", criterioEvId));
    }
}
