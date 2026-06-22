package com.coagronet.validator.inventario.entidades;

import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.presentacionProducto.PresentacionProducto;
import com.coagronet.presentacionProducto.repositories.PresentacionProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductoPresentacionValidator {
    private final PresentacionProductoRepository presentacionProductoRepository;

    public PresentacionProducto validarProductoPresentacion(Long productoPresentacionId, Long empresaId){
        return presentacionProductoRepository
                .findByIdAndEmpresaId(productoPresentacionId,empresaId)
                .orElseThrow(() -> new BadRequestException("producto-presentacion.not-found", productoPresentacionId));
    }
}
