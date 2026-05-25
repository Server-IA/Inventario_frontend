package com.coagronet.validator.parametrizacion.entidades;

import com.coagronet.exceptionHandler.custom.BadRequestException;
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
                .orElseThrow(() -> new BadRequestException("tipo-movimiento.not-found" + tipoMovimientoId));
    }
}
