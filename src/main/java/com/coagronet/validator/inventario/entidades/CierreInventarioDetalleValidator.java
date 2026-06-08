package com.coagronet.validator.inventario.entidades;

import com.coagronet.cierreinventariodetalle.CierreInventarioDetalle;
import com.coagronet.cierreinventariodetalle.repositories.CierreInventarioDetalleRepository;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.validator.common.BaseValidator;
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
