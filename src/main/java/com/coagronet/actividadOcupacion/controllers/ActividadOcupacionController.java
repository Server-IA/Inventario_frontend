package com.coagronet.actividadOcupacion.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.actividadOcupacion.dtos.ActividadOcupacionMinimalDTO;
import com.coagronet.actividadOcupacion.mappers.ActividadOcupacionMapper;
import com.coagronet.actividadOcupacion.repositories.ActividadOcupacionRepository;

@RestController
@RequestMapping("/api/v1/actividad_ocupacion")
@CrossOrigin(origins = "*")
public class ActividadOcupacionController {

    private final ActividadOcupacionRepository actividadOcupacionRepository;
    private final ActividadOcupacionMapper actividadOcupacionMapper;

    private ActividadOcupacionController(
            ActividadOcupacionRepository actividadOcupacionRepository,
            ActividadOcupacionMapper actividadOcupacionMapper) {
        this.actividadOcupacionRepository = actividadOcupacionRepository;
        this.actividadOcupacionMapper = actividadOcupacionMapper;
    }

    @GetMapping("/minimal")
    private ResponseEntity<List<ActividadOcupacionMinimalDTO>> findAllMinimal() {

        List<ActividadOcupacionMinimalDTO> actividadOcupacionMinimalDTOs = actividadOcupacionRepository
                .findAllByOrderByIdAsc()
                .stream()
                .map(actividadOcupacionMapper::toMinimalDTO)
                .collect(Collectors.toList());

        return !actividadOcupacionMinimalDTOs.isEmpty()
                ? ResponseEntity.ok(actividadOcupacionMinimalDTOs)
                : ResponseEntity.noContent().build();
    }

}
