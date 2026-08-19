package com.inventario.tipoDispositivo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.inventario.tipoDispositivo.dtos.TipoDispositivoDTO;
import com.inventario.tipoDispositivo.services.TipoDispositivoService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/v3/tipo_dispositivo")
@RequiredArgsConstructor
public class TipoDispositivoController {

    private final TipoDispositivoService tipoDispositivoService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<TipoDispositivoDTO>> findAll() {
        return ResponseEntity.ok(tipoDispositivoService.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<TipoDispositivoDTO> findById(@PathVariable Long requestedId) {
        return tipoDispositivoService.findById(requestedId).map(ResponseEntity::ok)
            .orElse((ResponseEntity.notFound().build()));
    }

    @PostMapping
    public ResponseEntity<Void> createTipoDispositivo(@Valid @RequestBody TipoDispositivoDTO tipoDispositivoDTO,
        UriComponentsBuilder ucb) {
        return ResponseEntity
                    .created(uriBuilderUtil
                            .buildTipoDispositivoUri((tipoDispositivoService.create(tipoDispositivoDTO)).getId(), ucb))
                            .build();
        }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateTipoDispositivo(@PathVariable Long requestedId,
        @Valid @RequestBody TipoDispositivoDTO tipoDispositivoDTO) {
            tipoDispositivoService.update(requestedId, tipoDispositivoDTO);
            return ResponseEntity.noContent().build();
        }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoDispositivo(@PathVariable Long id) {
        tipoDispositivoService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
