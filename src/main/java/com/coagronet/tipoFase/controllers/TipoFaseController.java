package com.coagronet.tipoFase.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.tipoFase.dtos.TipoFaseDTO;
import com.coagronet.tipoFase.services.TipoFaseService;
import com.coagronet.utils.UriBuilderUtil;


import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/v3/tipo_fase")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoFaseController {

    private final TipoFaseService tipoFaseService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<TipoFaseDTO>> findAll() {
        return ResponseEntity.ok(tipoFaseService.findAll());
    }


    @GetMapping("/{requestedId}")
    public ResponseEntity<TipoFaseDTO> findById(@PathVariable Long requestedId) {
        return tipoFaseService.findById(requestedId).map(ResponseEntity::ok)
            .orElse((ResponseEntity.notFound().build()));
    }

    @PostMapping
    public ResponseEntity<Void> createTipoFase(@Valid @RequestBody TipoFaseDTO tipoFaseDTO,
            UriComponentsBuilder ucb) {
        return ResponseEntity
                .created(uriBuilderUtil
                        .buildTipoFaseUri((tipoFaseService.create(tipoFaseDTO)).getId(),ucb))
                        .build();
        }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateTipoFase(@PathVariable Long requestedId,
        @Valid @RequestBody TipoFaseDTO tipoFaseDTO) {
            tipoFaseService.update(requestedId, tipoFaseDTO);
            return ResponseEntity.noContent().build();
        }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoFase(@PathVariable Long id) {
        tipoFaseService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
