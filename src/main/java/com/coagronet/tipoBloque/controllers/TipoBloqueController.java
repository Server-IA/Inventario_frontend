package com.coagronet.tipoBloque.controllers;

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

import com.coagronet.tipoBloque.dtos.TipoBloqueDTO;
import com.coagronet.tipoBloque.services.TipoBloqueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tipo_bloque")
@RequiredArgsConstructor
public class TipoBloqueController {


    private final TipoBloqueService tipoBloqueService;


    @GetMapping("/{requestedId}")
    public ResponseEntity<TipoBloqueDTO> findById(@PathVariable Long requestedId) {
        return tipoBloqueService.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TipoBloqueDTO>> findAll() {
        List<TipoBloqueDTO> tipoBloques = tipoBloqueService.findAll();
        return tipoBloques.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tipoBloques);
    }

    @PostMapping
    public ResponseEntity<Void> createTipoBloque(@RequestBody TipoBloqueDTO newTipoBloqueRequest,
                                                 UriComponentsBuilder ucb) {
        TipoBloqueDTO savedTipoBloque = tipoBloqueService.create(newTipoBloqueRequest);
        URI locationOfNewTipoBloque = ucb
                .path("/api/v1/tipo_bloque/{id}")
                .buildAndExpand(savedTipoBloque.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewTipoBloque).build();
    }

    @GetMapping("/minimal")
    public ResponseEntity<List<TipoBloqueDTO>> findAllMinimal() {
        List<TipoBloqueDTO> tipoBloqueDTOs = tipoBloqueService.findAllMinimal();
        return tipoBloqueDTOs.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tipoBloqueDTOs);
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putTipoBloque(@PathVariable Long requestedId,
            @RequestBody TipoBloqueDTO tipoBloqueDTOUpdate) {
        tipoBloqueService.update(requestedId, tipoBloqueDTOUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoBloque(@PathVariable Long id) {
        tipoBloqueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}