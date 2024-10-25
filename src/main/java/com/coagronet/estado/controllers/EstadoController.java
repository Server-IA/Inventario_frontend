package com.coagronet.estado.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.coagronet.estado.dtos.DatosListadoEstado;
import com.coagronet.estado.repositories.EstadoRepository;

@RestController
@RequestMapping("/api/v1/estados")
@CrossOrigin(origins = "*")
public class EstadoController {

    private final EstadoRepository estadoRepository;

    public EstadoController(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Estado> findById(@PathVariable Integer requestedId) {
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
    public ResponseEntity<List<DatosListadoEstado>> listadoEstados() {
        List<Estado> estados = estadoRepository.findAll();
        List<DatosListadoEstado> datosListadoEstados = estados.stream().map(DatosListadoEstado::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(datosListadoEstados);
    }

    @PostMapping
    public ResponseEntity<Void> createEstado(@RequestBody Estado newEstadoRequest, UriComponentsBuilder ucb) {
        Estado estado = new Estado(null, newEstadoRequest.getNombre(),
                newEstadoRequest.getDescripcion(), newEstadoRequest.getAcronimo());
        Estado savedEstado = estadoRepository.save(estado);
        URI locationOfNewEstado = ucb.path("/api/v1/estados/{id}")
                .buildAndExpand(savedEstado.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewEstado).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Object> putEstado(@PathVariable Integer requestedId, @RequestBody Estado estadoUpdate) {
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
    public ResponseEntity<Void> deleteEstado(@PathVariable Integer id) {
        if (estadoRepository.existsById(id)) {
            estadoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
