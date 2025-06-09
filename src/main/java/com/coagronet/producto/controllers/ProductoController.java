package com.coagronet.producto.controllers;

import java.net.URI;
import java.util.List;

import com.coagronet.producto.services.ProductoService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.producto.dtos.ProductoDTO;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> findAll() {
        List<ProductoDTO> productoDTOList = productoService.findAll();

        return productoDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(productoDTOList);
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<ProductoDTO> findById(@PathVariable Long requestedId) {
        return productoService.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<ProductoDTO> createProducto(@RequestBody @Valid ProductoDTO newProductoRequest,
            UriComponentsBuilder ucb) {
        ProductoDTO savedProductoDTO = productoService.create(newProductoRequest);

        URI locationOfNewPedido = uriBuilderUtil.buildProductoUri(savedProductoDTO.getId(), ucb);

        return ResponseEntity.created(locationOfNewPedido).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putProducto(@PathVariable Long requestedId,
            @RequestBody @Valid ProductoDTO productoDTOUpdate) {

        productoService.update(requestedId, productoDTOUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteProducto(@PathVariable Long id) {

        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
