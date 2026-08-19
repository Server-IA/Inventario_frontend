package com.inventario.validator.inventario.entidades;

import com.inventario.produccion.exception.FechaInvalidaException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.produccion.Produccion;
import com.inventario.produccion.repositories.ProduccionRepository;
import com.inventario.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProduccionValidator implements BaseValidator {

    private final ProduccionRepository produccionRepository;

    public Produccion validarProduccion(Long produccionId, Long empresaId) {
        return produccionRepository.findByIdAndEmpresaId(produccionId, empresaId)
                .orElseThrow(() -> new BadRequestException("produccion.not-found: " + produccionId));
    }

    public void validarFechasDeProduccion(Produccion produccion) {
        if (produccion.getFechaInicio().isAfter(produccion.getFechaFinal())) {
            throw new FechaInvalidaException("validation.fecha-rango.invalid");
        }
    }

}
