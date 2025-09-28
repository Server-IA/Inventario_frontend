package com.coagronet.entidadvalidator.entidades;

import com.coagronet.entidadvalidator.constantes.MensajesValidaciones;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.repositories.KardexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KardexValidator {

    private final KardexRepository kardexRepository;

    public Kardex validarKardex(Long kardexId, Long empresaId) {
        return kardexRepository.findByIdAndEmpresaId(kardexId, empresaId)
                .orElseThrow(() -> new BadRequestException(MensajesValidaciones.KARDEX_NO_VALIDO));
    }

}
