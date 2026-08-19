package com.inventario.validator.inventario.entidades;

import com.inventario.evaluacion.Evaluacion;
import com.inventario.evaluacion.repositories.EvaluacionRepository;
import com.inventario.exceptionHandler.NotFoundException;
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
