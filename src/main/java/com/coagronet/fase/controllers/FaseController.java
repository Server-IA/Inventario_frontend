package com.coagronet.fase.controllers;

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

import com.coagronet.fase.dtos.FaseDTO;
import com.coagronet.fase.services.FaseService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fase")
@CrossOrigin("*")
@RequiredArgsConstructor
public class FaseController {

    private final FaseService faseService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<FaseDTO>> findAll() {
        return ResponseEntity.ok(faseService.findAll());
    }


    @GetMapping("/{requestedId}")
    public ResponseEntity<FaseDTO> findById(@PathVariable Long requestedId) {
        return faseService.findById(requestedId).map(ResponseEntity::ok)
            .orElse((ResponseEntity.notFound().build()));
    }

    @PostMapping
    public ResponseEntity<Void> createFase(@Valid @RequestBody FaseDTO faseDTO,
        UriComponentsBuilder ucb) {
            return ResponseEntity
            .created(uriBuilderUtil
                    .buildFaseUri((faseService.create(faseDTO)).getId(), ucb))
                    .build();
        }
    
    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateFase(@PathVariable Long requestedId,
        @Valid @RequestBody FaseDTO faseDTO) {
            faseService.update(requestedId, faseDTO);
            return ResponseEntity.noContent().build();
        }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFase(@PathVariable Long id) {
        faseService.delete(id);
        return ResponseEntity.noContent().build();
    }


    
}
