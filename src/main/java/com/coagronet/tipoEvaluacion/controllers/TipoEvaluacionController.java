package com.coagronet.tipoEvaluacion.controllers;

import com.coagronet.criterioEvaluacion.dtos.CriterioEvaluacionDTO;
import com.coagronet.tipoEvaluacion.dtos.TipoEvaluacionDTO;
import com.coagronet.tipoEvaluacion.mappers.TipoEvaluacionMapper;
import com.coagronet.tipoEvaluacion.repositories.TipoEvaluacionRepository;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tipo_evaluacion")
@CrossOrigin(origins = "*")
public class TipoEvaluacionController {

    private final TipoEvaluacionRepository tipoEvaluacionRepository;
    private final TipoEvaluacionMapper tipoEvaluacionMapper;


    private TipoEvaluacionController(
            TipoEvaluacionRepository tipoEvaluacionRepository,
            TipoEvaluacionMapper tipoEvaluacionMapper) {
        this.tipoEvaluacionRepository = tipoEvaluacionRepository;
        this.tipoEvaluacionMapper = tipoEvaluacionMapper;
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

    @GetMapping
    private ResponseEntity<List<?>> findAll() {
        List<TipoEvaluacionDTO> tipoEvaluacionDTOList = tipoEvaluacionRepository
                .findByEstadoIdNotOrderByIdAsc( 2)
                .stream()
                .map(tipoEvaluacionMapper::toDTO)
                .toList();

        return !tipoEvaluacionDTOList.isEmpty()
                ? ResponseEntity.ok(tipoEvaluacionDTOList)
                : ResponseEntity.noContent().build();
    }

}
