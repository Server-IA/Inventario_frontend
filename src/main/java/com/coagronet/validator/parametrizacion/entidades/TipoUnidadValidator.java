package com.coagronet.validator.parametrizacion.entidades;

import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.tipounidad.TipoUnidad;
import com.coagronet.tipounidad.repositories.TipoUnidadRepository;
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
