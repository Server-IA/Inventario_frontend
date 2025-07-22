package com.coagronet.unidad.controllers;

import java.net.URI;
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

import com.coagronet.unidad.dtos.UnidadDTO;
import com.coagronet.unidad.services.UnidadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/unidad")
@RequiredArgsConstructor
public class UnidadController {

    private final UnidadService unidadService;




    @GetMapping("/{requestedId}")
    public ResponseEntity<UnidadDTO> findById(@PathVariable Long requestedId) {
        return unidadService.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UnidadDTO>> findAll() {
        List<UnidadDTO> unidadDTOList = unidadService.findAll();

        return unidadDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(unidadDTOList);
    }

    @PostMapping
    public ResponseEntity<Void> createUnidad(@Valid @RequestBody UnidadDTO newUnidadRequest,
            UriComponentsBuilder ucb) {

        UnidadDTO savedUnidad = unidadService.create(newUnidadRequest);

        URI locationOfNewUnidad = ucb
                .path("/{id}")
                .buildAndExpand(savedUnidad.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewUnidad).build();
    }



    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putUnidad(@PathVariable Long requestedId, @Valid @RequestBody UnidadDTO unidadDTOUpdate) {

        unidadService.update(requestedId, unidadDTOUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnidad(@PathVariable Long id) {
        unidadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
