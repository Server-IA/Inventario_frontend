package com.coagronet.tipoSede.controllers;

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
import com.coagronet.tipoSede.TipoSede;
import com.coagronet.tipoSede.dtos.TipoSedeDTO;
import com.coagronet.tipoSede.mappers.TipoSedeMapper;
import com.coagronet.tipoSede.repositories.TipoSedeRepository;
import com.coagronet.tipoSede.services.TipoSedeService;

@RestController
@RequestMapping("/api/v1/tipo_sede")
@CrossOrigin(origins = "*")
public class TipoSedeController {
    @Autowired
    private TipoSedeService tipoSedeService;

    @Autowired
    private TipoSedeRepository tipoSedeRepository;

    @Autowired
    private TipoSedeMapper tipoSedeMapper;

    @Autowired
    private EstadoRepository estadoRepository;

    @GetMapping
    private ResponseEntity<Page<TipoSedeDTO>> findAll(@PageableDefault Pageable pageable) {
        return ResponseEntity
                .ok(tipoSedeRepository.findByEstadoNot(2, pageable).map(TipoSedeMapper.INSTANCE::toDTO));
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<TipoSedeDTO> findById(@PathVariable Integer requestedId) {
        TipoSede tipoSede = tipoSedeRepository.findByIdAndEstadoNot(requestedId, 2);
        TipoSedeDTO tipoSedeDTO = tipoSedeMapper.toDTO(tipoSede);
        if (tipoSede != null) {
            return ResponseEntity.ok(tipoSedeDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createTipoSede(@RequestBody TipoSedeDTO tipoSedeDTO,
            UriComponentsBuilder ucb) {
        TipoSede tipoSede = tipoSedeMapper.toEntity(tipoSedeDTO);
        tipoSedeRepository.save(tipoSede);
        URI locationOfNewTipoSede = ucb
                .path("/api/v1/tipo_sede/{id}")
                .buildAndExpand(tipoSede.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewTipoSede).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putTipoSede(@PathVariable Integer requestedId,
            @RequestBody TipoSedeDTO tipoSedeUpdate) {
        TipoSede tipoSede = tipoSedeMapper.toEntity(tipoSedeUpdate);
        tipoSedeRepository.findByIdAndEstadoNot(requestedId, 2);
        if (null != tipoSede) {
            tipoSede.setId(requestedId);
            tipoSedeRepository.save(tipoSede);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteTipoSede(@PathVariable Integer id) {
        if (tipoSedeRepository.existsByIdAndEstadoNot(id, 2)) {
            TipoSede tipoSede = tipoSedeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("TipoSede not found with id: " + id));
            Estado nuevoEstado = estadoRepository.findById(2)
                    .orElseThrow(() -> new RuntimeException("Estado not found with id: 2"));
            tipoSede.setEstado(nuevoEstado);
            tipoSedeRepository.save(tipoSede);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
