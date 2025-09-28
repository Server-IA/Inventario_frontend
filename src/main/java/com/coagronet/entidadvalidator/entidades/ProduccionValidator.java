package com.coagronet.entidadvalidator.entidades;

import com.coagronet.entidadvalidator.constantes.MensajesValidaciones;
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
                .orElseThrow(() -> new BadRequestException(MensajesValidaciones.PRODUCCION_NO_VALIDO));
    }

}
