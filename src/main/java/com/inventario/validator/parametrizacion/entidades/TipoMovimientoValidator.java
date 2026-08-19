package com.inventario.validator.parametrizacion.entidades;

import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.tipoMovimiento.TipoMovimiento;
import com.inventario.tipoMovimiento.repositories.TipoMovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TipoMovimientoValidator {

    private final TipoMovimientoRepository tipoMovimientoRepository;

    public TipoMovimiento validarTipoMovimiento(Long tipoMovimientoId, Long empresaId) {
        return tipoMovimientoRepository.findByIdAndEmpresaId(tipoMovimientoId, empresaId)
                .orElseThrow(() -> new BadRequestException("tipo-movimiento.not-found: " + tipoMovimientoId));
    }
}
