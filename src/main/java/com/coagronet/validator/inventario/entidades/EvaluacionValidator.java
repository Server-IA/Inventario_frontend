package com.coagronet.validator.inventario.entidades;

import com.coagronet.evaluacion.Evaluacion;
import com.coagronet.evaluacion.repositories.EvaluacionRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionValidator {
    private final EvaluacionRepository evaluacionRepository;


    public Evaluacion validarEvaluacionPorEmpresa(Long evaluacionId, Long empresaId){
        return evaluacionRepository.findByIdAndEmpresaId(evaluacionId, empresaId)
                .orElseThrow(()-> new NotFoundException("evaluacion.not-found", evaluacionId));
    }
}
