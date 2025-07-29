package com.coagronet.programacion.controllers;

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

import com.coagronet.programacion.dtos.ProgramacionDTO;
import com.coagronet.programacion.services.ProgramacionService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/programacion")
@RequiredArgsConstructor
public class ProgramacionController {
    private final ProgramacionService programacionService;
    private final UriBuilderUtil uriBuilderUtil;

        @GetMapping
        public ResponseEntity<List<ProgramacionDTO>> findAll(){
            return ResponseEntity.ok(programacionService.findAll());
        }

        @GetMapping("/{requestedId}")
        public ResponseEntity<ProgramacionDTO> findById(@PathVariable Long requestedId) {
            return programacionService.findById(requestedId).map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        public ResponseEntity<Void> createProgramacion(@Valid @RequestBody ProgramacionDTO programacionDTO,
            UriComponentsBuilder ucb) {
                return ResponseEntity
                    .created(uriBuilderUtil
                            .buildProgramacionUri((programacionService.create(programacionDTO)).getId(), ucb))
                            .build();
            }
        
        
        @PutMapping("/{requestedId}")
        public ResponseEntity<Void> updateProgramacion(@PathVariable Long requestedId,
            @Valid @RequestBody ProgramacionDTO programacionDTO) {
                programacionService.update(requestedId, programacionDTO);
                return ResponseEntity.noContent().build();
            }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProgramacion(@PathVariable Long id) {
            programacionService.delete(id);
            return ResponseEntity.noContent().build();
        }
}
