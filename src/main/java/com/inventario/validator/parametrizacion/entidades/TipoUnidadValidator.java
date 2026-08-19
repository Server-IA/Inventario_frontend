package com.inventario.validator.parametrizacion.entidades;

import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.tipounidad.TipoUnidad;
import com.inventario.tipounidad.repositories.TipoUnidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TipoUnidadValidator {

    private final TipoUnidadRepository tipoUnidadRepository;

    public TipoUnidad validarTipoUnidad(Long tipoUnidad) {
        return tipoUnidadRepository.findById(tipoUnidad)
                .orElseThrow(() -> new BadRequestException("tipo-unidad.not-found: " + tipoUnidad));

    }

}
