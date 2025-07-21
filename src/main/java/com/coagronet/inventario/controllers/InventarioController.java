package com.coagronet.inventario.controllers;

import com.coagronet.inventario.services.InventarioService;
import com.coagronet.inventario.dtos.InventarioDTO;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;
    private final UriBuilderUtil uriBuilderUtil;


    @GetMapping
    public ResponseEntity<List<InventarioDTO>> findAll(){
        List<InventarioDTO> inventarioDTOList = inventarioService.findAll();

        return inventarioDTOList.isEmpty()?
                ResponseEntity.noContent().build():
                ResponseEntity.ok(inventarioDTOList);
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<InventarioDTO> findById(@PathVariable Long requestedId){
        return inventarioService.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InventarioDTO> createInventario
            (@RequestBody @Valid InventarioDTO newInventario, UriComponentsBuilder ucb){

        InventarioDTO savedInventarioDTO = inventarioService.create(newInventario);

        URI locationOfNewInventario = uriBuilderUtil.buildInventarioUri(savedInventarioDTO.getId(), ucb);

        return ResponseEntity.created(locationOfNewInventario).build();

    }


    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putInventario(@PathVariable Long requestedId,
                                               @RequestBody @Valid InventarioDTO inventarioDTOUpdate) {
        inventarioService.update(requestedId, inventarioDTOUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteInventario(@PathVariable Long id) {

        inventarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
