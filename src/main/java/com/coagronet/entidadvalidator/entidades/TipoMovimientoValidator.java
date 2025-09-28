package com.coagronet.entidadvalidator.entidades;

import com.coagronet.entidadvalidator.constantes.MensajesValidaciones;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.tipoMovimiento.repositories.TipoMovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TipoMovimientoValidator {

    private final TipoMovimientoRepository tipoMovimientoRepository;


    public TipoMovimiento validarTipoMovimiento(Long tipoMovimientoId, Long empresaId) {
        return tipoMovimientoRepository.findByIdAndEmpresaId(tipoMovimientoId, empresaId)
                .orElseThrow(() -> new BadRequestException(MensajesValidaciones.TIPO_MOVIMIENTO_NO_VALIDO));
    }
}
