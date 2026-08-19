package com.inventario.validator.inventario.entidades;

import com.inventario.articuloOrdenCompra.ArticuloOrdenCompra;
import com.inventario.articuloOrdenCompra.repositories.ArticuloOrdenCompraRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.ordenCompra.OrdenCompra;
import com.inventario.ordenCompra.repositories.OrdenCompraRepository;
import com.inventario.validator.common.BaseValidator;
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
