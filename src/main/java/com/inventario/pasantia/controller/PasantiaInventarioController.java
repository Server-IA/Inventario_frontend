package com.inventario.pasantia.controller;

import java.util.List;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.inventario.pasantia.dto.InventarioAsignadoDTO;
import com.inventario.pasantia.dto.InventarioCreateRequestDTO;
import com.inventario.pasantia.dto.InventarioProgresoRequestDTO;
import com.inventario.pasantia.dto.InventarioProgresoResponseDTO;
import com.inventario.pasantia.dto.MensajeResponseDTO;
import com.inventario.pasantia.service.PasantiaInventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventarios")
@RequiredArgsConstructor
public class PasantiaInventarioController {

    private final PasantiaInventarioService inventarioService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> crearInventario(@Valid @RequestBody InventarioCreateRequestDTO request) {
        Long idNuevoInventario = inventarioService.crearInventario(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(idNuevoInventario)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/asignados")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InventarioAsignadoDTO>> getInventariosAsignados() {
        return ResponseEntity.ok(inventarioService.getInventariosAsignados());
    }

    @GetMapping("/{inventarioId}/progreso")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InventarioProgresoResponseDTO> getProgresoInventario(@PathVariable Long inventarioId) {
        return ResponseEntity.ok(inventarioService.getProgresoByInventarioId(inventarioId));
    }

    @PostMapping("/{inventarioId}/progreso")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MensajeResponseDTO> guardarProgreso(@PathVariable Long inventarioId,
            @RequestBody InventarioProgresoRequestDTO request) {
        return ResponseEntity.ok(inventarioService.guardarProgreso(inventarioId, request));
    }
    @PostMapping("/{inventarioId}/finalizar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MensajeResponseDTO> finalizarInventario(@PathVariable Long inventarioId,
            @RequestBody InventarioProgresoRequestDTO request) {
        return ResponseEntity.ok(inventarioService.finalizarInventario(inventarioId, request));
    }
}
