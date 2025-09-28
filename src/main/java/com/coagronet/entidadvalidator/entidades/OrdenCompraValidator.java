package com.coagronet.entidadvalidator.entidades;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdenCompraValidator {

    private final OrdenCompraRepository ordenCompraRepository;

    public OrdenCompra validarOrdenCompra(Long ordenCompraId, Long empresaId) {
        return ordenCompraRepository.findByIdAndEmpresaId(ordenCompraId, empresaId)
                .orElseThrow(() -> new NotFoundException("orden-compra.not-found", ordenCompraId));
    }
}
