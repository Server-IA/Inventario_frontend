package com.inventario.validator.inventario.entidades;

import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.presentacionProducto.PresentacionProducto;
import com.inventario.presentacionProducto.repositories.PresentacionProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductoPresentacionValidator {
    private final PresentacionProductoRepository presentacionProductoRepository;

    public PresentacionProducto validarProductoPresentacion(Long productoPresentacionId, Long empresaId) {
        return presentacionProductoRepository
                .findByIdAndEmpresaId(productoPresentacionId, empresaId)
                .orElseThrow(() -> new BadRequestException(
                        "producto-presentacion.not-found: " + productoPresentacionId));
    }
}
