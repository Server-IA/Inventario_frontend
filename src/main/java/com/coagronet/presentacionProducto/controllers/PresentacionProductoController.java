package com.coagronet.presentacionProducto.controllers;

import java.net.URI;
import java.util.List;

import com.coagronet.presentacionProducto.services.PresentacionProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import com.coagronet.presentacionProducto.dtos.PresentacionProductoDTO;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/producto_presentacion")
@RequiredArgsConstructor
public class PresentacionProductoController {

    private final PresentacionProductoService presentacionProductoService;

    @GetMapping
    private ResponseEntity<List<PresentacionProductoDTO>> findAll() {
        List<PresentacionProductoDTO> presentacionProductoDTOS = presentacionProductoService.findAll();

        return presentacionProductoDTOS.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(presentacionProductoDTOS);
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<PresentacionProductoDTO> findById(@PathVariable Long requestedId) {
        return presentacionProductoService.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createPresentacionProducto(
            @Valid @RequestBody PresentacionProductoDTO newpresentacionProductoDTO,
            UriComponentsBuilder ucb) {
        PresentacionProductoDTO savedPresentacionProducto = presentacionProductoService
                .create(newpresentacionProductoDTO);
        URI locationOfNewPresentacionProducto = ucb
                .path("/{id}")
                .buildAndExpand(savedPresentacionProducto.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewPresentacionProducto).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putPresentacionProducto(@PathVariable Long requestedId,
            @Valid @RequestBody PresentacionProductoDTO presentacionProductoUpdate) {

        presentacionProductoService.update(requestedId, presentacionProductoUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deletePresentacionProducto(@PathVariable Long id) {

        presentacionProductoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
