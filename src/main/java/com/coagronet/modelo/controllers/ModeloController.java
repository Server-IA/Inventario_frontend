package com.coagronet.modelo.controllers;

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

import com.coagronet.modelo.dtos.ModeloDTO;
import com.coagronet.modelo.services.ModeloService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/modelo")
@RequiredArgsConstructor
public class ModeloController {
    
    private final ModeloService modeloService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<ModeloDTO>> findAll() {
        return ResponseEntity.ok(modeloService.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<ModeloDTO> findById(@PathVariable Long requestedId) {
        return modeloService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createModelo(@Valid @RequestBody ModeloDTO modeloDTO,
        UriComponentsBuilder ucb) {
            return ResponseEntity
                .created(uriBuilderUtil
                        .buildModeloUri((modeloService.create(modeloDTO).getId()), ucb))
                        .build();
        }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateModelo(@PathVariable Long requestedId,
        @Valid @RequestBody ModeloDTO modeloDTO) {
            modeloService.update(requestedId, modeloDTO);
            return ResponseEntity.noContent().build();
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModelo(@PathVariable Long id) {
        modeloService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
