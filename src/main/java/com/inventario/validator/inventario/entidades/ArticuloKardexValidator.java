package com.inventario.validator.inventario.entidades;

import com.inventario.articuloKardex.ArticuloKardex;
import com.inventario.articuloKardex.repositories.ArticuloKardexRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticuloKardexValidator implements BaseValidator {
    private final ArticuloKardexRepository articuloKardexRepository;


    public ArticuloKardex validarArticuloKardex(Long articuloKardexId, Long empresaId){
        return articuloKardexRepository.findByIdAndEmpresaId(articuloKardexId, empresaId)
                .orElseThrow(() -> new NotFoundException("El artículo de kardex no fue encontrado."));
    }
}
