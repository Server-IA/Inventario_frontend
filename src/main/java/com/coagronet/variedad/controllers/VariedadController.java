package com.coagronet.variedad.controllers;

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

import com.coagronet.utils.UriBuilderUtil;
import com.coagronet.variedad.dtos.VariedadDTO;
import com.coagronet.variedad.services.VariedadService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/variedad")
@CrossOrigin(origins="*")
@RequiredArgsConstructor
public class VariedadController {
    
    private final VariedadService variedadService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<VariedadDTO>> findAll() {
        return ResponseEntity.ok(variedadService.findAll());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<VariedadDTO> findById(@PathVariable Long requestedId) {
        return variedadService.findById(requestedId).map(ResponseEntity::ok)
            .orElse((ResponseEntity.notFound().build()));
    }

    @PostMapping
    public ResponseEntity<Void> createVariedad(@Valid @RequestBody VariedadDTO variedadDTO, 
            UriComponentsBuilder ucb) {
        return ResponseEntity
            .created(uriBuilderUtil
                    .buildVariedadUri((variedadService.create(variedadDTO)).getId(),ucb))
            .build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateVariedad(@PathVariable Long requestedId,
        @Valid @RequestBody VariedadDTO variedadDTO) {
            variedadService.update(requestedId, variedadDTO);
            return ResponseEntity.noContent().build();
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariedad(@PathVariable Long id) {
        variedadService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    
}


