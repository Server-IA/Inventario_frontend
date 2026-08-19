package com.inventario.dispositivoMedicion.controllers;

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

import com.inventario.dispositivoMedicion.dtos.DispositivoMedicionDTO;
import com.inventario.dispositivoMedicion.services.DispositivoMedicionService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/dispositivo_medicion")
@RequiredArgsConstructor
public class DispositivoMedicionController {
    
    private final DispositivoMedicionService dispositivoMedicionService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<DispositivoMedicionDTO>> findAll(){
        return ResponseEntity.ok(dispositivoMedicionService.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<DispositivoMedicionDTO> findById(@PathVariable Long requestedId) {
        return dispositivoMedicionService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createDispositivoMedicion(@Valid @RequestBody DispositivoMedicionDTO dispositivoMedicionDTO,
        UriComponentsBuilder ucb) {
            return ResponseEntity
                .created(uriBuilderUtil
                        .buildDispositivoMedicionUri((dispositivoMedicionService.create(dispositivoMedicionDTO)).getId(), ucb))
                        .build();
        }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateDispositivoMedicion(@PathVariable Long requestedId,
        @Valid @RequestBody DispositivoMedicionDTO dispositivoMedicionDTO) {
            dispositivoMedicionService.update(requestedId, dispositivoMedicionDTO);
            return ResponseEntity.noContent().build();
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDispositivoMedicion(@PathVariable Long id) {
        dispositivoMedicionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
