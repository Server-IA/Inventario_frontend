package com.coagronet.estado.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.coagronet.estado.Estado;
import com.coagronet.estado.dtos.EstadoDTO;
import com.coagronet.estado.mappers.EstadoMapper;
import com.coagronet.estado.repositories.EstadoRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/estado")
public class EstadoController {

    private final EstadoMapper estadoMapper;
    private final EstadoRepository estadoRepository;

    @GetMapping("/{requestedId}")
    public ResponseEntity<Estado> findById(@PathVariable Long requestedId) {
        return estadoRepository.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Estado>> findAll(Pageable pageable) {
        Page<Estado> page = estadoRepository.findAll(pageable);
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/short")
    public ResponseEntity<List<EstadoDTO>> listadoEstados() {
        List<Estado> estados = estadoRepository.findAll();
        List<EstadoDTO> datosListadoEstados = estados.stream()
                .map(estadoMapper::toShortDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(datosListadoEstados);
    }

    @PostMapping
    public ResponseEntity<Void> createEstado(@RequestBody @Valid EstadoDTO newEstadoRequest, UriComponentsBuilder ucb) {

        Estado savedEstado = estadoRepository.save(estadoMapper.toEntity(newEstadoRequest));
        URI locationOfNewEstado = ucb.path("/api/v1/estados/{id}")
                .buildAndExpand(savedEstado.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewEstado).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Object> putEstado(@PathVariable Long requestedId, @RequestBody Estado estadoUpdate) {
        return estadoRepository.findById(requestedId)
                .map(estado -> {
                    estado.setNombre(estadoUpdate.getNombre());
                    estado.setDescripcion(estadoUpdate.getDescripcion());
                    estado.setAcronimo(estadoUpdate.getAcronimo());
                    estadoRepository.save(estado);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstado(@PathVariable Long id) {
        if (estadoRepository.existsById(id)) {
            estadoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
