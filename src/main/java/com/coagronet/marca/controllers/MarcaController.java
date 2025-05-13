package com.coagronet.marca.controllers;

import com.coagronet.utils.UriBuilderUtil;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.marca.dtos.MarcaDTO;
import com.coagronet.marca.services.MarcaService;

@RestController
@RequestMapping("/api/v1/marca")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MarcaController {

        private final MarcaService marcaService;
        private final UriBuilderUtil uriBuilderUtil;

        @GetMapping
        public ResponseEntity<List<MarcaDTO>> findAll() {
                return ResponseEntity.ok(marcaService.findAll());
        }

        @GetMapping(params = "available=true")
        public ResponseEntity<List<MarcaDTO>> findAllAvailable() {
                return ResponseEntity.ok(marcaService.findAllAvailable());
        }

        @GetMapping("/{requestedId}")
        public ResponseEntity<MarcaDTO> findById(
                        @PathVariable Long requestedId) {
                return marcaService.findById(requestedId)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        public ResponseEntity<Void> createMarca(
                        @Valid @RequestBody MarcaDTO marcaDTO,
                        UriComponentsBuilder ucb) {
                MarcaDTO savedMarca = marcaService.create(
                                marcaDTO);
                URI locationOfNewMarca = uriBuilderUtil.buildMarcaUri(
                                savedMarca.getId(),
                                ucb);
                return ResponseEntity.created(locationOfNewMarca).build();
        }

        @PutMapping("/{requestedId}")
        public ResponseEntity<Void> updateMarca(
                        @PathVariable Long requestedId,
                        @Valid @RequestBody MarcaDTO marcaDTO) {
                marcaService.update(requestedId, marcaDTO);
                return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteMarca(
                        @PathVariable Long id) {
                marcaService.delete(id);
                return ResponseEntity.noContent().build();
        }

}
