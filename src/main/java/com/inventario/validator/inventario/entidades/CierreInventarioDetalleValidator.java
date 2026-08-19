package com.inventario.validator.inventario.entidades;

import com.inventario.cierreinventariodetalle.CierreInventarioDetalle;
import com.inventario.cierreinventariodetalle.repositories.CierreInventarioDetalleRepository;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CierreInventarioDetalleValidator implements BaseValidator {

    private final CierreInventarioDetalleRepository cierreInventarioDetalleRepository;

    public CierreInventarioDetalle validarCierreDetalle(Long cierreInventarioDetalleId, Long empresaId) {
        return cierreInventarioDetalleRepository.findByIdAndEmpresaId(cierreInventarioDetalleId, empresaId)
                .orElseThrow(() -> new BadRequestException(
                        "cierre-inventario-detalle.not-found: " + cierreInventarioDetalleId));
    }

}
