package com.coagronet.tipoGenerico.controllers;

import java.net.URI;
import java.util.List;

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

import com.coagronet.tipoGenerico.dtos.TipoGenericoDTO;
import com.coagronet.tipoGenerico.services.TipoGenericoService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/v1/tipo/{table}")
@RequiredArgsConstructor
public class TipoGenericoController {

    private final TipoGenericoService tipoGenericoService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<TipoGenericoDTO>> findAll(@PathVariable String table) {
        return ResponseEntity.ok(tipoGenericoService.findAll(table));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoGenericoDTO> findById(@PathVariable String table, @PathVariable Long id) {
        return tipoGenericoService.findById(table, id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> create(@PathVariable String table,
                                       @Valid @RequestBody TipoGenericoDTO dto,
                                       UriComponentsBuilder ucb) {
        TipoGenericoDTO created = tipoGenericoService.create(table, dto);
        URI location = uriBuilderUtil.buildTipoGenericoUri(table, created, ucb);
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String table, @PathVariable Long id,
                                       @Valid @RequestBody TipoGenericoDTO dto) {
        tipoGenericoService.update(table, id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String table, @PathVariable Long id) {
        tipoGenericoService.delete(table, id);
        return ResponseEntity.noContent().build();
    }
}
