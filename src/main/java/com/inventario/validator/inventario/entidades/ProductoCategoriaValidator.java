package com.inventario.validator.inventario.entidades;

import org.springframework.stereotype.Component;

import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.productoCategoria.ProductoCategoria;
import com.inventario.productoCategoria.repositories.ProductoCategoriaRepository;

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
