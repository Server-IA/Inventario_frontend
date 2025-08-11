package com.coagronet.produccionDesarrollo.controllers;

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

import com.coagronet.produccionDesarrollo.dtos.ProduccionDesarrolloDTO;
import com.coagronet.produccionDesarrollo.services.ProduccionDesarrolloService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/produccion_desarrollo")
@RequiredArgsConstructor
public class ProduccionDesarrolloController {
    
    private final ProduccionDesarrolloService produccionDesarrolloService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<ProduccionDesarrolloDTO>> findAll(){
        return ResponseEntity.ok(produccionDesarrolloService.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<ProduccionDesarrolloDTO> findById(@PathVariable Long requestedId) {
        return produccionDesarrolloService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createProduccionDesarrollo(@Valid @RequestBody ProduccionDesarrolloDTO produccionDesarrolloDTO,
        UriComponentsBuilder ucb) {
            return ResponseEntity
                .created(uriBuilderUtil
                        .buildProduccionDesarrolloUri((produccionDesarrolloService.create(produccionDesarrolloDTO).getId()), ucb))
                        .build();
        }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateProduccionDesarrollo(@PathVariable Long requestedId,
        @Valid @RequestBody ProduccionDesarrolloDTO produccionDesarrolloDTO) {
            produccionDesarrolloService.update(requestedId, produccionDesarrolloDTO);
            return ResponseEntity.noContent().build();
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduccionDesarrollo(@PathVariable Long id) {
        produccionDesarrolloService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
