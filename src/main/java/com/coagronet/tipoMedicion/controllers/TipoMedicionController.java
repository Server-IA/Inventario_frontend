package com.coagronet.tipoMedicion.controllers;

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

import com.coagronet.tipoMedicion.dtos.TipoMedicionDTO;
import com.coagronet.tipoMedicion.services.TipoMedicionService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/tipo_medicion")
@CrossOrigin("*")
@RequiredArgsConstructor
public class TipoMedicionController {
    
    private final TipoMedicionService tipoMedicionService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<TipoMedicionDTO>> findAll() {
        return ResponseEntity.ok(tipoMedicionService.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<TipoMedicionDTO> findById(@PathVariable Long requestedId) {
        return tipoMedicionService.findById(requestedId).map(ResponseEntity::ok)
            .orElse((ResponseEntity.notFound().build()));
    }

    @PostMapping
    public ResponseEntity<Void> createTipoMedicion(@Valid @RequestBody TipoMedicionDTO tipoMedicionDTO,
        UriComponentsBuilder ucb) {
            return ResponseEntity
            .created(uriBuilderUtil
                    .buildTipoMedicionUri((tipoMedicionService.create(tipoMedicionDTO)).getId(), ucb))
                    .build();
        }
    
    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateTipoMedicion(@PathVariable Long requestedId,
        @Valid @RequestBody TipoMedicionDTO tipoMedicionDTO) {
            tipoMedicionService.update(requestedId, tipoMedicionDTO);
            return ResponseEntity.noContent().build();
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoMedicion(@PathVariable Long id) {
        tipoMedicionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
