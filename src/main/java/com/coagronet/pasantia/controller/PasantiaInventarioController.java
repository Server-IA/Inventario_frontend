package com.coagronet.pasantia.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.pasantia.dto.InventarioAsignadoDTO;
import com.coagronet.pasantia.dto.InventarioProgresoResponseDTO;
import com.coagronet.pasantia.service.PasantiaInventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventarios")
@RequiredArgsConstructor
public class PasantiaInventarioController {

    private final PasantiaInventarioService inventarioService;

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
}
