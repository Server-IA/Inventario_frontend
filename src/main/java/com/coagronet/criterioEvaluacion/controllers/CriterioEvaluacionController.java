package com.coagronet.criterioEvaluacion.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.criterioEvaluacion.CriterioEvaluacion;
import com.coagronet.criterioEvaluacion.dtos.CriterioEvaluacionDTO;
import com.coagronet.criterioEvaluacion.mappers.CriterioEvaluacionMapper;
import com.coagronet.criterioEvaluacion.repositirories.CriterioEvaluacionRepository;
import com.coagronet.tipoEvaluacion.repositories.TipoEvaluacionRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/criterio_evaluacion")
@CrossOrigin(origins = "*")
public class CriterioEvaluacionController {

    private final CriterioEvaluacionRepository criterioEvaluacionRepository;
    private final CriterioEvaluacionMapper criterioEvaluacionMapper;
    private final TipoEvaluacionRepository tipoEvaluacionRepository;

    private CriterioEvaluacionController(
            CriterioEvaluacionRepository criterioEvaluacionRepository,
            CriterioEvaluacionMapper criterioEvaluacionMapper,
            TipoEvaluacionRepository tipoEvaluacionRepository) {
        this.criterioEvaluacionRepository = criterioEvaluacionRepository;
        this.criterioEvaluacionMapper = criterioEvaluacionMapper;
        this.tipoEvaluacionRepository = tipoEvaluacionRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<?> findById(@PathVariable Integer requestedId) {
        return criterioEvaluacionRepository.findById(requestedId)
                .map(criterioEvaluacionMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<?> createCriterioEvaluacion(@RequestBody CriterioEvaluacionDTO criterioEvaluacionDTO) {
        // Verifica si ya existe un CriterioEvaluacion con el mismo TipoEvaluacion y
        // Estado diferente
        if (criterioEvaluacionRepository.existsByTipoEvaluacionIdAndEstadoIdNot(
                criterioEvaluacionDTO.getTipoEvaluacion(),
                criterioEvaluacionDTO.getEstado())) {
            return ResponseEntity.badRequest().build();
        }
        // Verifica si el TipoEvaluacion especificado en el DTO existe en el repositorio
        if (!tipoEvaluacionRepository.existsById(criterioEvaluacionDTO.getTipoEvaluacion())) {
            return ResponseEntity.badRequest().build();
        }
        // Guarda el nuevo CriterioEvaluacion en el repositorio
        CriterioEvaluacion savedEntity = criterioEvaluacionRepository
                .save(criterioEvaluacionMapper.toEntity(criterioEvaluacionDTO));
        // Devuelve una respuesta 201 (Created) con la ubicación del nuevo recurso y el
        // objeto CriterioEvaluacionDTO creado
        return ResponseEntity.created(URI.create("/api/v1/criterio_evaluacion/" + savedEntity.getId()))
                .body(criterioEvaluacionMapper.toDTO(savedEntity));
    }

    @GetMapping("/tipoEvaluacionId/{requestedTipoEvaluacionId}")
    private ResponseEntity<List<?>> findAllByTipoEvaluacionId(@PathVariable Integer requestedTipoEvaluacionId) {
        List<CriterioEvaluacionDTO> criterioEvaluacionDTOList = criterioEvaluacionRepository
                .findByTipoEvaluacionIdAndEstadoIdNotOrderByIdAsc(requestedTipoEvaluacionId, 2)
                .stream()
                .map(criterioEvaluacionMapper::toDTO)
                .toList();

        return !criterioEvaluacionDTOList.isEmpty()
                ? ResponseEntity.ok(criterioEvaluacionDTOList)
                : ResponseEntity.noContent().build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCriterioEvaluacion(
            @PathVariable Integer requestedId,
            @RequestBody CriterioEvaluacionDTO criterioEvaluacionDTOUpdate) {
        // Verifica si el CriterioEvaluacion especificado en el DTO existe en el
        // repositorio
        if (criterioEvaluacionRepository.existsById(requestedId)
                && tipoEvaluacionRepository.existsByIdAndEstadoIdNot(
                        criterioEvaluacionDTOUpdate.getTipoEvaluacion(),
                        2)) {
            // Actualiza el CriterioEvaluacion en el repositorio
            CriterioEvaluacionDTO updateCriterioEvaluacionDTO = new CriterioEvaluacionDTO(
                    requestedId,
                    criterioEvaluacionDTOUpdate.getTipoEvaluacion(),
                    criterioEvaluacionDTOUpdate.getNombre(),
                    criterioEvaluacionDTOUpdate.getDescripcion(),
                    criterioEvaluacionDTOUpdate.getEstado());
            criterioEvaluacionRepository.save(criterioEvaluacionMapper.toEntity(updateCriterioEvaluacionDTO));
            // Devuelve una respuesta 204 (No Content)
            return ResponseEntity.noContent().build();
        }
        // Devuelve una respuesta 404 (Not Found)
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCriterioEvaluacion(@PathVariable Integer id) {
        // Verifica si el CriterioEvaluacion especificado en el DTO existe en el
        // repositorio
        if (criterioEvaluacionRepository.existsById(id)) {
            // Elimina el CriterioEvaluacion del repositorio
            criterioEvaluacionRepository.deleteById(id);
            // Devuelve una respuesta 204 (No Content)
            return ResponseEntity.noContent().build();
        }
        // Devuelve una respuesta 404 (Not Found)
        return ResponseEntity.notFound().build();
    }

}
