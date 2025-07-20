package com.coagronet.produccionFase.controllers;

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

import com.coagronet.produccionFase.dtos.ProduccionFaseDTO;
import com.coagronet.produccionFase.services.ProduccionFaseService;
import com.coagronet.reports.services.ReportService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/produccion_fase")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProduccionFaseController {

    private final ProduccionFaseService produccionFaseService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<ProduccionFaseDTO>> findAll() {
        return ResponseEntity.ok(produccionFaseService.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<ProduccionFaseDTO> findById(@PathVariable Long requestedId) {
        return produccionFaseService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createProduccionFase(@Valid @RequestBody ProduccionFaseDTO produccionFaseDTO,
            UriComponentsBuilder ucb) {
                return ResponseEntity
                    .created(uriBuilderUtil
                            .buildProduccionFaseUri((produccionFaseService.create(produccionFaseDTO)).getId(), ucb))
                            .build();
            }
    
    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateProduccionFase(@PathVariable Long requestedId,
        @Valid @RequestBody ProduccionFaseDTO produccionFaseDTO) {
                produccionFaseService.update(requestedId, produccionFaseDTO);
                return ResponseEntity.noContent().build();
            }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduccionFase(@PathVariable Long id) {
        produccionFaseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    
    
}
