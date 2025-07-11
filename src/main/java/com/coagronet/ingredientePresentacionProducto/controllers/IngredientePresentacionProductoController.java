package com.coagronet.ingredientePresentacionProducto.controllers;

import java.util.List;

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

import com.coagronet.ingredientePresentacionProducto.dtos.IngredientePresentacionProductoDTO;
import com.coagronet.ingredientePresentacionProducto.services.IngredientePresentacionProductoService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ingrediente-presentacion-producto")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class IngredientePresentacionProductoController {

    private final IngredientePresentacionProductoService ingredientePresentacionProductoService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<IngredientePresentacionProductoDTO>> findAll() {
        return ResponseEntity.ok(ingredientePresentacionProductoService.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<IngredientePresentacionProductoDTO> findById(@PathVariable Long requestedId) {
        return ingredientePresentacionProductoService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createIngredientePresentacionProducto(
            @Valid @RequestBody IngredientePresentacionProductoDTO ingredientePresentacionProductoDTO,
            UriComponentsBuilder ucb) {
        return ResponseEntity
                .created(uriBuilderUtil
                        .buildIngredientePresentacionProductoUri(
                                (ingredientePresentacionProductoService.create(ingredientePresentacionProductoDTO)).getId(), ucb))
                .build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateIngredientePresentacionProducto(@PathVariable Long requestedId,
            @Valid @RequestBody IngredientePresentacionProductoDTO ingredientePresentacionProductoDTO) {
        ingredientePresentacionProductoService.update(requestedId, ingredientePresentacionProductoDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredientePresentacionProducto(@PathVariable Long id) {
        ingredientePresentacionProductoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
