package com.coagronet.tipoEspacio.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.coagronet.tipoEspacio.TipoEspacio;
import com.coagronet.tipoEspacio.dtos.TipoEspacioDTO;
import com.coagronet.tipoEspacio.mappers.TipoEspacioMapper;
import com.coagronet.tipoEspacio.repositories.TipoEspacioRepository;

@RestController
@RequestMapping("/api/v1/tipo_espacio")
@CrossOrigin(origins = "*")
public class TipoEspacioController {

    @Autowired
    private TipoEspacioRepository tipoEspacioRepository;

    @Autowired
    private TipoEspacioMapper tipoEspacioMapper;

    @Autowired
    private EstadoRepository estadoRepository;

    @GetMapping
    private ResponseEntity<Page<TipoEspacioDTO>> findAll(@PageableDefault Pageable pageable) {
        return ResponseEntity
                .ok(tipoEspacioRepository.findByEstadoNot(2, pageable).map(TipoEspacioMapper.INSTANCE::toDTO));
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<TipoEspacioDTO> findById(@PathVariable Integer requestedId) {
        TipoEspacio tipoEspacio = tipoEspacioRepository.findByIdAndEstadoNot(requestedId, 2);
        TipoEspacioDTO tipoEspacioDTO = tipoEspacioMapper.toDTO(tipoEspacio);
        if (tipoEspacio != null) {
            return ResponseEntity.ok(tipoEspacioDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createTipoEspacio(@RequestBody TipoEspacioDTO tipoEspacioDTO,
            UriComponentsBuilder ucb) {
        TipoEspacio tipoEspacio = tipoEspacioMapper.toEntity(tipoEspacioDTO);
        tipoEspacioRepository.save(tipoEspacio);
        URI locationOfNewTipoEspacio = ucb
                .path("/api/v1/tipo_espacio/{id}")
                .buildAndExpand(tipoEspacio.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewTipoEspacio).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putTipoEspacio(@PathVariable Integer requestedId,
            @RequestBody TipoEspacioDTO tipoEspacioUpdate) {
        TipoEspacio tipoEspacio = tipoEspacioMapper.toEntity(tipoEspacioUpdate);
        tipoEspacioRepository.findByIdAndEstadoNot(requestedId, 2);
        if (null != tipoEspacio) {
            tipoEspacio.setId(requestedId);
            tipoEspacioRepository.save(tipoEspacio);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteTipoEspacio(@PathVariable Integer id) {
        if (tipoEspacioRepository.existsByIdAndEstadoNot(id, 2)) {
            TipoEspacio tipoEspacio = tipoEspacioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("TipoEspacio not found with id: " + id));
            Estado nuevoEstado = estadoRepository.findById(2)
                    .orElseThrow(() -> new RuntimeException("Estado not found with id: 2"));
            tipoEspacio.setEstado(nuevoEstado);
            tipoEspacioRepository.save(tipoEspacio);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
