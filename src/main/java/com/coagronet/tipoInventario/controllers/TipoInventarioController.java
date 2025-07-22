package com.coagronet.tipoInventario.controllers;

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

import com.coagronet.tipoInventario.dtos.TipoInventarioDTO;
import com.coagronet.tipoInventario.services.TipoInventarioService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tipo_inventario")
@RequiredArgsConstructor
public class TipoInventarioController {


    private final TipoInventarioService tipoInventarioService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<TipoInventarioDTO>> findAll(){
        List<TipoInventarioDTO> tipoInventarioDTOList = tipoInventarioService.findAll();

        return tipoInventarioDTOList.isEmpty()?
                ResponseEntity.noContent().build():
                ResponseEntity.ok(tipoInventarioDTOList);
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<TipoInventarioDTO> findById(@PathVariable Long requestedId){
        return tipoInventarioService.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoInventarioDTO> createTipoInventario
            (@RequestBody @Valid TipoInventarioDTO newTipoInventario, UriComponentsBuilder ucb){

        TipoInventarioDTO savedTipoInventarioDTO = tipoInventarioService.create(newTipoInventario);

        URI locationOfNewTipoInventario = uriBuilderUtil.buildTipoInventarioUri(savedTipoInventarioDTO.getId(), ucb);

        return ResponseEntity.created(locationOfNewTipoInventario).build();

    }


    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putTipoInventario(@PathVariable Long requestedId,
                                             @RequestBody @Valid TipoInventarioDTO tipoInventarioDTOUpdate) {

        tipoInventarioService.update(requestedId, tipoInventarioDTOUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteTipoInventario(@PathVariable Long id) {

        tipoInventarioService.delete(id);
        return ResponseEntity.noContent().build();
    }



}
