package com.coagronet.validator.inventario.entidades;

import org.springframework.stereotype.Component;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.productoCategoria.ProductoCategoria;
import com.coagronet.productoCategoria.repositories.ProductoCategoriaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductoCategoriaValidator {

    private final ProductoCategoriaRepository productoCategoriaRepository;

    public ProductoCategoria validarProductoCategoriaPorEmpresa(Long productoCategoriaId, Long empresaId) {
        return productoCategoriaRepository.findByIdAndEmpresaId(productoCategoriaId, empresaId)
                .orElseThrow(() -> new NotFoundException("producto-categoria.not-found", productoCategoriaId));
    }

}
