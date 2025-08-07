package com.coagronet.sector.controllers;

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

import com.coagronet.sector.dtos.SectorDTO;
import com.coagronet.sector.services.SectorService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/sector")
@RequiredArgsConstructor
public class SectorController {

    private final SectorService sectorService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<SectorDTO>> findAll() {
        return ResponseEntity.ok(sectorService.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<SectorDTO> findById(@PathVariable Long requestedId) {
        return sectorService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Void> createSector(@Valid @RequestBody SectorDTO sectorDTO,
        UriComponentsBuilder ucb) {
            return ResponseEntity
                .created(uriBuilderUtil
                        .buildSectorUri((sectorService.create(sectorDTO).getId()), ucb))
                        .build();
        }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateSector(@PathVariable Long requestedId,
        @Valid @RequestBody SectorDTO sectorDTO) {
            sectorService.update(requestedId, sectorDTO);
            return ResponseEntity.noContent().build();
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSector(@PathVariable Long id) {
        sectorService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
