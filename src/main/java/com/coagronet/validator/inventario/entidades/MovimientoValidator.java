package com.coagronet.validator.inventario.entidades;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.movimiento.Movimiento;
import com.coagronet.movimiento.repositories.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class MovimientoValidator {

    private final MovimientoRepository movimientoRepository;

    public Movimiento validarMovimiento(Long movimientoId) {
        return movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new NotFoundException("movimiento.not-found", movimientoId));
    }


}