package com.coagronet.tipoEvaluacion.controllers;

import com.coagronet.tipoEvaluacion.dtos.TipoEvaluacionDTO;
import com.coagronet.tipoEvaluacion.services.TipoEvaluacionService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

@RestController
@RequestMapping("/api/v1/tipo-evaluacion")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoEvaluacionController {

        private final TipoEvaluacionService tipoEvaluacionService;
        private final UriBuilderUtil uriBuilderUtil;

        @GetMapping
        public ResponseEntity<List<TipoEvaluacionDTO>> findAll() {
                return ResponseEntity.ok(tipoEvaluacionService.findAll());
        }

        @GetMapping("/{requestedId}")
        public ResponseEntity<TipoEvaluacionDTO> findById(@PathVariable Long requestedId) {
                return tipoEvaluacionService.findById(requestedId).map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        public ResponseEntity<Void> createTipoEvaluacion(
                        @Valid @RequestBody TipoEvaluacionDTO tipoEvaluacionDTO,
                        UriComponentsBuilder ucb) {
                return ResponseEntity.created(uriBuilderUtil.buildTipoEvaluacionUri(
                                tipoEvaluacionService.create(
                                                tipoEvaluacionDTO).getId(),
                                ucb)).build();
        }

        @PutMapping("/{requestedId}")
        public ResponseEntity<Void> updateTipoEvaluacion(
                        @PathVariable Long requestedId,
                        @Valid @RequestBody TipoEvaluacionDTO tipoEvaluacionDTO) {
                tipoEvaluacionService.update(requestedId, tipoEvaluacionDTO);
                return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteTipoEvaluacion(@PathVariable Long id) {
                tipoEvaluacionService.delete(id);
                return ResponseEntity.noContent().build();
        }

}
