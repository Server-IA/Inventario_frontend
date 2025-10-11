package com.coagronet.validator.inventario.entidades;

import com.coagronet.evaluacionitem.EvaluacionItem;
import com.coagronet.evaluacionitem.repositories.EvaluacionItemRepository;
import com.coagronet.exceptionHandler.NotFoundException;
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
