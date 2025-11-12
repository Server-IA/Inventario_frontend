package com.coagronet.validator.inventario.entidades;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.validator.common.BaseValidator;
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
