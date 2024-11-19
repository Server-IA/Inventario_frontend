package com.coagronet.tipoProduccion.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.tipoProduccion.TipoProduccion;
import com.coagronet.tipoProduccion.dtos.TipoProduccionDTO;
import com.coagronet.tipoProduccion.mappers.TipoProduccionMapper;
import com.coagronet.tipoProduccion.repositories.TipoProduccionRepository;

@RestController
@RequestMapping("/api/v1/tipo_produccion")
@CrossOrigin(origins = "*")
public class TipoProduccionController {
    private final TipoProduccionRepository tipoProduccionRepository;
    private final TipoProduccionMapper tipoProduccionMapper;
    private final EstadoRepository estadoRepository;

    @Autowired
    public TipoProduccionController(
            TipoProduccionRepository tipoProduccionRepository,
            TipoProduccionMapper tipoProduccionMapper,
            EstadoRepository estadoRepository) {
        this.tipoProduccionRepository = tipoProduccionRepository;
        this.tipoProduccionMapper = tipoProduccionMapper;
        this.estadoRepository = estadoRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<TipoProduccionDTO> findById(@PathVariable Integer requestedId) {
        TipoProduccion tipoProduccion = tipoProduccionRepository.findByIdAndEstadoIdNot(requestedId, 2);
        TipoProduccionDTO tipoProduccionDTO = tipoProduccionMapper.toDto(tipoProduccion);
        if (tipoProduccion != null) {
            return ResponseEntity.ok(tipoProduccionDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> createTipoProduccion(
            @RequestBody TipoProduccionDTO newTipoProduccionRequest,
            UriComponentsBuilder ucb) {
        TipoProduccion tipoProduccion = tipoProduccionMapper.toEntity(newTipoProduccionRequest);

        // Asegurarse de que el estado existe
        Estado estado = estadoRepository.findById(newTipoProduccionRequest.getEstado())
                .orElseThrow(() -> new RuntimeException("Estado not found"));
        tipoProduccion.setEstado(estado);

        tipoProduccion = tipoProduccionRepository.save(tipoProduccion);

        URI locationOfNewTipoProduccion = ucb
                .path("/api/v1/tipo_produccion/{id}")
                .buildAndExpand(tipoProduccion.getId())
                .toUri();

        return ResponseEntity.created(locationOfNewTipoProduccion).build();
    }

    @GetMapping
    public ResponseEntity<List<TipoProduccionDTO>> findAll() {
        List<TipoProduccionDTO> tipoProduccionDTOs = tipoProduccionRepository
                .findByEstadoIdNotOrderByTipoIdAsc(2)
                .stream()
                .map(tipoProduccionMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tipoProduccionDTOs);
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putTipoProduccion(
            @PathVariable Integer requestedId,
            @RequestBody TipoProduccionDTO tipoProduccionUpdate) {
        if (!tipoProduccionRepository.existsById(requestedId)) {
            return ResponseEntity.notFound().build();
        }

        TipoProduccion tipoProduccion = tipoProduccionMapper.toEntity(tipoProduccionUpdate);
        tipoProduccion.setId(requestedId);

        // Asegurarse de que el estado existe
        Estado estado = estadoRepository.findById(tipoProduccionUpdate.getEstado())
                .orElseThrow(() -> new RuntimeException("Estado not found"));
        tipoProduccion.setEstado(estado);

        tipoProduccionRepository.save(tipoProduccion);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoProduccion(@PathVariable Integer id) {
        if (tipoProduccionRepository.existsByIdAndEstadoIdNot(id, 2)) {
            TipoProduccion tipoProduccion = tipoProduccionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("TipoProduccion not found with id: " + id));

            Estado nuevoEstado = estadoRepository.findById(2)
                    .orElseThrow(() -> new RuntimeException("Estado not found with id: 2"));

            tipoProduccion.setEstado(nuevoEstado);
            tipoProduccionRepository.save(tipoProduccion);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
