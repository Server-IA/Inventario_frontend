package com.inventario.subSeccionDispositivo.controllers;

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

import com.inventario.subSeccionDispositivo.dtos.SubseccionDispositivoDTO;
import com.inventario.subSeccionDispositivo.services.SubseccionDispositivoService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/sub_seccion_dispositivo")
@RequiredArgsConstructor
public class SubseccionDispositivoController {

    private final SubseccionDispositivoService subseccionDispositivoService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<SubseccionDispositivoDTO>> findAll() {
        return ResponseEntity.ok(subseccionDispositivoService.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<SubseccionDispositivoDTO> findById(@PathVariable Long requestedId) {
        return subseccionDispositivoService.findById(requestedId).map(ResponseEntity::ok)
                .orElse((ResponseEntity.notFound().build()));
    }

    @PostMapping
    public ResponseEntity<Void> createSubseccionDispositivo(@Valid @RequestBody SubseccionDispositivoDTO subseccionDispositivoDTO,
        UriComponentsBuilder ucb) {
            return ResponseEntity
                    .created(uriBuilderUtil
                            .buildSubseccionDispositivoUri((subseccionDispositivoService.create(subseccionDispositivoDTO)).getId(), ucb))
                            .build();
        }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateSubseccionDispositivo(@PathVariable Long requestedId,
        @Valid @RequestBody SubseccionDispositivoDTO subseccionDispositivoDTO) {
            subseccionDispositivoService.update(requestedId, subseccionDispositivoDTO);
            return ResponseEntity.noContent().build();
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubseccionDispositivo(@PathVariable Long id) {
        subseccionDispositivoService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
