package com.inventario.tipoModelo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.inventario.tipoModelo.dtos.TipoModeloDTO;
import com.inventario.tipoModelo.services.TipoModeloService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/tipo_modelo")
@RequiredArgsConstructor
public class TipoModeloController {
    private final TipoModeloService tipoModeloService;
    private final UriBuilderUtil uriBuilderUtil;
    @GetMapping
    public ResponseEntity<List<TipoModeloDTO>> findAll() {
        return ResponseEntity.ok(tipoModeloService.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<TipoModeloDTO> findById(@PathVariable Long requestedId) {
        return tipoModeloService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createTipoModelo(@Valid @RequestBody TipoModeloDTO tipoModeloDTO,
        UriComponentsBuilder ucb) {
            return ResponseEntity
                .created(uriBuilderUtil
                        .buildTipoModeloUri((tipoModeloService.create(tipoModeloDTO)).getId(), ucb))
                        .build();
        }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateTipoModelo(@PathVariable Long requestedId,
        @Valid @RequestBody TipoModeloDTO tipoModeloDTO) {
            tipoModeloService.update(requestedId, tipoModeloDTO);
            return ResponseEntity.noContent().build();
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoModelo(@PathVariable Long id) {
        tipoModeloService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
