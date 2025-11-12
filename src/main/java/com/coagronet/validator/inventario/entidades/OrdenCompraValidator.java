package com.coagronet.validator.inventario.entidades;

import com.coagronet.articuloOrdenCompra.ArticuloOrdenCompra;
import com.coagronet.articuloOrdenCompra.repositories.ArticuloOrdenCompraRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import com.coagronet.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdenCompraValidator implements BaseValidator {

    private final OrdenCompraRepository ordenCompraRepository;
    private final ArticuloOrdenCompraRepository articuloOrdenCompraRepository;

    public OrdenCompra validarOrdenCompra(Long ordenCompraId, Long empresaId) {
        return ordenCompraRepository.findByIdAndEmpresaId(ordenCompraId, empresaId)
                .orElseThrow(() -> new NotFoundException("orden-compra.not-found", ordenCompraId));
    }

    public ArticuloOrdenCompra validarArticulosOrdenCompra(Long ordenCompraId, Long empresaId) {
        return articuloOrdenCompraRepository.findByIdAndEmpresaId(ordenCompraId, empresaId)
                .orElseThrow(() -> new NotFoundException("orden-compra.not-found", ordenCompraId));
    }

    public OrdenCompra validarOrdenCompraPorPedidoId(Long pedidoId, Long empresaId) {
        return ordenCompraRepository.findByPedidoIdAndEmpresaId(pedidoId, empresaId)
                .orElseThrow(() -> new NotFoundException("orden-compra.not-found"));
    }





}
