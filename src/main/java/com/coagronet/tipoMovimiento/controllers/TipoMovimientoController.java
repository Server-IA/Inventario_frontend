package com.coagronet.tipoMovimiento.controllers;

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
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.tipoMovimiento.dtos.TipoMovimientoDTO;
import com.coagronet.tipoMovimiento.dtos.TipoMovimientoMinimalDTO;
import com.coagronet.tipoMovimiento.mappers.TipoMovimientoMapper;
import com.coagronet.tipoMovimiento.reposritories.TipoMovimientoRepository;

@RestController
@RequestMapping("/api/v1/tipo_movimiento")
@CrossOrigin(origins = "*")
public class TipoMovimientoController {
    private final TipoMovimientoRepository tipoMovimientoRepository;
    private final TipoMovimientoMapper tipoMovimientoMapper;
    private final EstadoRepository estadoRepository;

    @Autowired
    public TipoMovimientoController(
            TipoMovimientoRepository tipoMovimientoRepository,
            TipoMovimientoMapper tipoMovimientoMapper,
            EstadoRepository estadoRepository) {
        this.tipoMovimientoRepository = tipoMovimientoRepository;
        this.tipoMovimientoMapper = tipoMovimientoMapper;
        this.estadoRepository = estadoRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<TipoMovimientoDTO> findById(@PathVariable Integer requestedId) {
        return tipoMovimientoRepository.findByIdAndEstadoIdNot(requestedId, 2)
                .map(tipoMovimientoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createTipoMovimiento(
            @RequestBody TipoMovimientoDTO newTipoMovimientoRequest,
            UriComponentsBuilder ucb) {
        // Validar que el estado exista
        Estado estado = estadoRepository.findById(newTipoMovimientoRequest.getEstado())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        TipoMovimiento tipoMovimiento = tipoMovimientoMapper.toEntity(newTipoMovimientoRequest);
        tipoMovimiento.setEstado(estado);

        TipoMovimiento savedTipoMovimiento = tipoMovimientoRepository.save(tipoMovimiento);

        URI locationOfNewTipoMovimiento = ucb
                .path("/api/v1/tipo_movimiento/{id}")
                .buildAndExpand(savedTipoMovimiento.getId())
                .toUri();

        return ResponseEntity.created(locationOfNewTipoMovimiento).build();
    }

    @GetMapping
    public ResponseEntity<List<TipoMovimientoDTO>> findAll() {
        List<TipoMovimientoDTO> tipoMovimientoDTOs = tipoMovimientoRepository
                .findByEstadoIdNotOrderByIdAsc(2)
                .stream()
                .map(tipoMovimientoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tipoMovimientoDTOs);
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putTipoMovimiento(
            @PathVariable Integer requestedId,
            @RequestBody TipoMovimientoDTO tipoMovimientoUpdate) {
        // Verificar que el registro exista
        TipoMovimiento existingTipoMovimiento = tipoMovimientoRepository.findById(requestedId)
                .orElseThrow(() -> new RuntimeException("Tipo Movimiento no encontrado"));

        // Validar que el estado exista
        Estado estado = estadoRepository.findById(tipoMovimientoUpdate.getEstado())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        // Mapear y actualizar
        TipoMovimiento tipoMovimiento = tipoMovimientoMapper.toEntity(tipoMovimientoUpdate);
        tipoMovimiento.setId(requestedId);
        tipoMovimiento.setEstado(estado);

        tipoMovimientoRepository.save(tipoMovimiento);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoMovimiento(@PathVariable Integer id) {
        // Verificar si existe y no está en estado eliminado
        if (tipoMovimientoRepository.existsByIdAndEstadoIdNot(id, 2)) {
            TipoMovimiento tipoMovimiento = tipoMovimientoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tipo Movimiento no encontrado"));

            // Cambiar estado a eliminado (generalmente estado con ID 2)
            Estado estadoEliminado = estadoRepository.findById(2)
                    .orElseThrow(() -> new RuntimeException("Estado eliminado no encontrado"));

            tipoMovimiento.setEstado(estadoEliminado);
            tipoMovimientoRepository.save(tipoMovimiento);

            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/minimal")
    public ResponseEntity<List<TipoMovimientoMinimalDTO>> findAllMinimal() {
        List<TipoMovimientoMinimalDTO> tipoMovimientoMinimalDTOs = tipoMovimientoRepository
                .findByEstadoIdNotOrderByIdAsc(2)
                .stream()
                .map(tipoMovimientoMapper::toMinimalDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tipoMovimientoMinimalDTOs);
    }

    // Método para obtener un DTO minimal por ID
    @GetMapping("/minimal/{requestedId}")
    public ResponseEntity<TipoMovimientoMinimalDTO> findMinimalById(@PathVariable Integer requestedId) {
        return tipoMovimientoRepository.findByIdAndEstadoIdNot(requestedId, 2)
                .map(tipoMovimientoMapper::toMinimalDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}