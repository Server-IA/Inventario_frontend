package com.coagronet.validator.inventario.entidades;

import com.coagronet.produccion.exception.FechaInvalidaException;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.produccion.Produccion;
import com.coagronet.produccion.repositories.ProduccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProduccionValidator {

    private final ProduccionRepository produccionRepository;

    public Produccion validarProduccion(Long produccionId, Long empresaId) {
        return produccionRepository.findByIdAndEmpresaId(produccionId, empresaId)
                .orElseThrow(() -> new BadRequestException("produccion.not-found", produccionId));
    }

    public void validarFechasDeProduccion(Produccion produccion){
        if(produccion.getFechaInicio().isAfter(produccion.getFechaFinal())){
            throw new FechaInvalidaException("validation.fecha-rango.invalid");
        }
    }

}
