package com.coagronet.productoPresentacion.controllers;

import java.net.URI;
import java.util.List;

import com.coagronet.productoPresentacion.services.ProductoPresentacionService;
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

import com.coagronet.productoPresentacion.dtos.ProductoPresentacionDTO;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/producto-presentacion")
@RequiredArgsConstructor
public class ProductoPresentacionController {

    private final ProductoPresentacionService productoPresentacionService;



    @GetMapping
    private ResponseEntity<List<ProductoPresentacionDTO>> findAll() {
        List<ProductoPresentacionDTO> productoPresentacionDTOS = productoPresentacionService.findAll();

        return productoPresentacionDTOS.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(productoPresentacionDTOS);
    }

    @GetMapping("/minimal")
    private ResponseEntity<List<ProductoPresentacionDTO>> findAllMinimal() {
        List<ProductoPresentacionDTO> productoPresentacionDTOS = productoPresentacionService.findAllMinimal();

        return productoPresentacionDTOS.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(productoPresentacionDTOS);
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<ProductoPresentacionDTO> findById(@PathVariable Long requestedId) {
        return productoPresentacionService.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createProductoPresentacion(
            @Valid @RequestBody ProductoPresentacionDTO newproductoPresentacionDTO,
            UriComponentsBuilder ucb) {
        ProductoPresentacionDTO savedProductoPresentacion = productoPresentacionService.create(newproductoPresentacionDTO);
        URI locationOfNewProductoPresentacion = ucb
                .path("/{id}")
                .buildAndExpand(savedProductoPresentacion.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewProductoPresentacion).build();
    }



    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putProductoPresentacion(@PathVariable Long requestedId,
                                                         @Valid @RequestBody ProductoPresentacionDTO productoPresentacionUpdate) {

        productoPresentacionService.update(requestedId, productoPresentacionUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteProductoPresentacion(@PathVariable Long id) {

        productoPresentacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
