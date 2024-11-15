package com.coagronet.marca.controllers;

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
import com.coagronet.marca.Marca;
import com.coagronet.marca.dtos.MarcaDTO;
import com.coagronet.marca.mappers.MarcaMapper;
import com.coagronet.marca.repositories.MarcaRepository;

@RestController
@RequestMapping("/api/v1/marcas")
@CrossOrigin(origins = "*")
public class MarcaController {

    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private MarcaMapper marcaMapper;

    @Autowired
    private EstadoRepository estadoRepository;

    @GetMapping
    private ResponseEntity<List<MarcaDTO>> findAll() {
        List<MarcaDTO> marcaDTOs = marcaRepository.findByEstadoNot(2)
                .stream()
                .map(MarcaMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(marcaDTOs);
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<MarcaDTO> findById(@PathVariable Long requestedId) {
        Marca marca = marcaRepository.findByIdAndEstadoNot(requestedId, 2);
        MarcaDTO marcaDTO = marcaMapper.toDTO(marca);
        if (marca != null) {
            return ResponseEntity.ok(marcaDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createMarca(@RequestBody MarcaDTO marcaDTO,
            UriComponentsBuilder ucb) {
        Marca marca = marcaMapper.toEntity(marcaDTO);
        marcaRepository.save(marca);
        URI locationOfNewMarca = ucb
                .path("/api/v1/marcas/{id}")
                .buildAndExpand(marca.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewMarca).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putMarca(@PathVariable Long requestedId,
            @RequestBody MarcaDTO marcaUpdate) {
        Marca marca = marcaMapper.toEntity(marcaUpdate);
        marcaRepository.findByIdAndEstadoNot(requestedId, 2);
        if (null != marca) {
            marca.setId(requestedId);
            marcaRepository.save(marca);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteMarca(@PathVariable Long id) {
        if (marcaRepository.existsByIdAndEstadoNot(id, 2)) {
            Marca marca = marcaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Marca not found with id: " + id));
            Estado nuevoEstado = estadoRepository.findById(2)
                    .orElseThrow(() -> new RuntimeException("Estado not found with id: 2"));
            marca.setEstado(nuevoEstado);
            marcaRepository.save(marca);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
