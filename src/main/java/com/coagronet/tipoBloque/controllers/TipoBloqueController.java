package com.coagronet.tipoBloque.controllers;

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
import com.coagronet.tipoBloque.TipoBloque;
import com.coagronet.tipoBloque.dtos.TipoBloqueDTO;
import com.coagronet.tipoBloque.mappers.TipoBloqueMapper;
import com.coagronet.tipoBloque.repositories.TipoBloqueRepository;

@RestController
@RequestMapping("/api/v1/tipo_bloque")
@CrossOrigin(origins = "*")
public class TipoBloqueController {

    @Autowired
    private TipoBloqueRepository tipoBloqueRepository;

    @Autowired
    private TipoBloqueMapper tipoBloqueMapper;

    @Autowired
    private EstadoRepository estadoRepository;

    @GetMapping
    private ResponseEntity<Page<TipoBloqueDTO>> findAll(@PageableDefault Pageable pageable) {
        return ResponseEntity
                .ok(tipoBloqueRepository.findByEstadoNot(2, pageable).map(TipoBloqueMapper.INSTANCE::toDTO));
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<TipoBloqueDTO> findById(@PathVariable Integer requestedId) {
        TipoBloque tipoBloque = tipoBloqueRepository.findByIdAndEstadoNot(requestedId, 2);
        TipoBloqueDTO tipoBloqueDTO = tipoBloqueMapper.toDTO(tipoBloque);
        if (tipoBloque != null) {
            return ResponseEntity.ok(tipoBloqueDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createTipoBloque(@RequestBody TipoBloqueDTO tipoBloqueDTO,
            UriComponentsBuilder ucb) {
        TipoBloque tipoBloque = tipoBloqueMapper.toEntity(tipoBloqueDTO);
        tipoBloqueRepository.save(tipoBloque);
        URI locationOfNewTipoBloque = ucb
                .path("/api/v1/tipo_bloque/{id}")
                .buildAndExpand(tipoBloque.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewTipoBloque).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putTipoBloque(@PathVariable Integer requestedId,
            @RequestBody TipoBloqueDTO tipoBloqueUpdate) {
        TipoBloque tipoBloque = tipoBloqueMapper.toEntity(tipoBloqueUpdate);
        tipoBloqueRepository.findByIdAndEstadoNot(requestedId, 2);
        if (null != tipoBloque) {
            tipoBloque.setId(requestedId);
            tipoBloqueRepository.save(tipoBloque);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteTipoBloque(@PathVariable Integer id) {
        if (tipoBloqueRepository.existsByIdAndEstadoNot(id, 2)) {
            TipoBloque tipoBloque = tipoBloqueRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("TipoBloque not found with id: " + id));
            Estado nuevoEstado = estadoRepository.findById(2)
                    .orElseThrow(() -> new RuntimeException("Estado not found with id: 2"));
            tipoBloque.setEstado(nuevoEstado);
            tipoBloqueRepository.save(tipoBloque);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}