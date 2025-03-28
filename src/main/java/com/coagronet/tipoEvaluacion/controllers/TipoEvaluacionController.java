package com.coagronet.tipoEvaluacion.controllers;

import com.coagronet.tipoEvaluacion.dtos.TipoEvaluacionDTO;
import com.coagronet.tipoEvaluacion.services.TipoEvaluacionService;
import com.coagronet.utils.UriBuilderUtil;

import lombok.RequiredArgsConstructor;

import java.net.URI;
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
@RequestMapping("/api/v1/tipo_evaluacion")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoEvaluacionController {

        private final TipoEvaluacionService tipoEvaluacionService;
        private final UriBuilderUtil uriBuilderUtil;

        @GetMapping("/all")
        public ResponseEntity<List<TipoEvaluacionDTO>> findAll() {
                return ResponseEntity.ok(tipoEvaluacionService.findAll());
        }

        @GetMapping("/available")
        public ResponseEntity<List<TipoEvaluacionDTO>> findAllAvailable() {
                return ResponseEntity.ok(tipoEvaluacionService.findAllAvailable());
        }

        @GetMapping("/{requestedId}")
        public ResponseEntity<TipoEvaluacionDTO> findById(@PathVariable Integer requestedId) {
                TipoEvaluacionDTO tipoEvaluacionDTO = tipoEvaluacionService.findById(requestedId);
                if (tipoEvaluacionDTO != null) {
                        return ResponseEntity.ok(tipoEvaluacionDTO);
                } else {
                        return ResponseEntity.notFound().build();
                }
        }

        @PostMapping
        public ResponseEntity<Void> createTipoEvaluacion(
                        @RequestBody TipoEvaluacionDTO newTipoEvaluacionDTORequest,
                        UriComponentsBuilder ucb) {
                TipoEvaluacionDTO savedTipoEvaluacion = tipoEvaluacionService.create(
                                newTipoEvaluacionDTORequest);
                URI locationOfNewTipoEvaluacion = uriBuilderUtil.buildTipoEvaluacionUri(
                                savedTipoEvaluacion.getId(),
                                ucb);
                return ResponseEntity.created(locationOfNewTipoEvaluacion).build();
        }

        @PutMapping("/{requestedId}")
        public ResponseEntity<Void> updateTipoEvaluacion(
                        @PathVariable Integer requestedId,
                        @RequestBody TipoEvaluacionDTO tipoEvaluacionUpdate) {
                boolean updated = tipoEvaluacionService.update(requestedId, tipoEvaluacionUpdate);
                if (updated) {
                        return ResponseEntity.noContent().build();
                }
                return ResponseEntity.notFound().build();
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteTipoEvaluacion(@PathVariable Integer id) {
                if (tipoEvaluacionService.delete(id)) {
                        return ResponseEntity.noContent().build();
                }
                return ResponseEntity.notFound().build();
        }

}
