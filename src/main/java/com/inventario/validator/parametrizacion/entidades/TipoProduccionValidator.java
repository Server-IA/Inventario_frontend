package com.inventario.validator.parametrizacion.entidades;

import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.tipoProduccion.TipoProduccion;
import com.inventario.tipoProduccion.repositories.TipoProduccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TipoProduccionValidator {

    private final TipoProduccionRepository tipoProduccionRepository;

    public TipoProduccion validarTipoProduccion(Long tipoProduccionId, Long empresaId) {
        return tipoProduccionRepository.findByIdAndEmpresaId(tipoProduccionId, empresaId)
                .orElseThrow(() -> new BadRequestException("tipo-produccion.not-found: " + tipoProduccionId));
    }
}
