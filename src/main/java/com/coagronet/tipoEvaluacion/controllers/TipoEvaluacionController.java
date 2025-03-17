package com.coagronet.tipoEvaluacion.controllers;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.tipoEvaluacion.TipoEvaluacion;
import com.coagronet.tipoEvaluacion.dtos.TipoEvaluacionDTO;
import com.coagronet.tipoEvaluacion.mappers.TipoEvaluacionMapper;
import com.coagronet.tipoEvaluacion.repositories.TipoEvaluacionRepository;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/tipo_evaluacion")
@CrossOrigin(origins = "*")
public class TipoEvaluacionController {

    private final TipoEvaluacionRepository tipoEvaluacionRepository;
    private final TipoEvaluacionMapper tipoEvaluacionMapper;
    private final EstadoRepository estadoRepository;


    private TipoEvaluacionController(
            TipoEvaluacionRepository tipoEvaluacionRepository,
            TipoEvaluacionMapper tipoEvaluacionMapper,
            EstadoRepository estadoRepository) {
        this.tipoEvaluacionRepository = tipoEvaluacionRepository;
        this.tipoEvaluacionMapper = tipoEvaluacionMapper;
        this.estadoRepository = estadoRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<TipoEvaluacionDTO> findById(@PathVariable Integer requestedId) {
        return tipoEvaluacionRepository.findByIdAndEstadoIdNot(
                        requestedId,
                        2)
                .map(tipoEvaluacionMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /* @PostMapping
    private ResponseEntity<Void> createTipoEvaluacion(
            @RequestBody TipoEvaluacionDTO newTipoEvaluacionRequest,
            UriComponentsBuilder ucb) {
        TipoEvaluacionDTO newTipoEvaluacion = new TipoEvaluacionDTO(
                null,
                newTipoEvaluacionRequest.getNombre(),
                newTipoEvaluacionRequest.getEstado());
        TipoEvaluacion savedTipoEvaluacion = tipoEvaluacionMapper.toEntity(newTipoEvaluacion);
        tipoEvaluacionRepository.save(savedTipoEvaluacion);
        URI locationOfNewTipoEvaluacion = ucb
                .path("/api/v1/tipo_evaluacion/{id}")
                .buildAndExpand(savedTipoEvaluacion.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewTipoEvaluacion).build();
    } */

}
