package com.inventario.validator.inventario.entidades;

import com.inventario.evaluacionitem.EvaluacionItem;
import com.inventario.evaluacionitem.repositories.EvaluacionItemRepository;
import com.inventario.exceptionHandler.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionItemValidator {
    private final EvaluacionItemRepository evaluacionItemRepository;


    public EvaluacionItem validarEvaluacionItemPorEmpresa(Long evaluacionItemId, Long empresaId){
        return evaluacionItemRepository.findByIdAndEmpresaId(evaluacionItemId, empresaId)
                .orElseThrow(()-> new NotFoundException("evaluacion-item.not-found", evaluacionItemId));
    }
}
