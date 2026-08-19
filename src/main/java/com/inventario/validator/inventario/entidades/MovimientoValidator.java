package com.inventario.validator.inventario.entidades;

import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.movimiento.Movimiento;
import com.inventario.movimiento.repositories.MovimientoRepository;
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