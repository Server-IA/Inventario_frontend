package com.coagronet.pasantia.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.pasantia.dto.ProductoItemDTO;
import com.coagronet.pasantia.dto.SubseccionDTO;
import com.coagronet.pasantia.service.PasantiaSubseccionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subsecciones")
@RequiredArgsConstructor
public class PasantiaSubseccionController {

    private final PasantiaSubseccionService subseccionService;

    @GetMapping("/{subSeccionId}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductoItemDTO>> getItemsBySubseccionId(
            @PathVariable("subSeccionId") Long subSeccionId) {
        return ResponseEntity.ok(subseccionService.getItemsBySubseccionId(subSeccionId));
    }

    @GetMapping(params = "fields=id,nombre")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SubseccionDTO>> getAllSubsecciones() {
        return ResponseEntity.ok(subseccionService.getAllSubsecciones());
    }
}
