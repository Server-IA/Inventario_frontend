package com.coagronet.rol.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.coagronet.rol.dtos.RolRequestDTO;
import com.coagronet.rol.dtos.RolResponseDTO;
import com.coagronet.rol.services.RolService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Validated
public class RolController {

    private final RolService rolService;

    // ====================== CREATE =======================

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody RolRequestDTO request) {
        RolResponseDTO created = rolService.create(request);

        URI location = URI.create(String.format("/api/v1/roles/%d", created.id()));

        return ResponseEntity.created(location).build();
    }

    // ====================== UPDATE =======================

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody RolRequestDTO request) {

        rolService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    // ====================== GET BY ID =======================

    @GetMapping("/{id}")
    public ResponseEntity<RolResponseDTO> getById(@PathVariable Long id) {
        RolResponseDTO rol = rolService.getById(id);
        return ResponseEntity.ok(rol);
    }

    // ====================== LISTAR =======================

    @GetMapping
    public ResponseEntity<List<RolResponseDTO>> getAll() {
        List<RolResponseDTO> roles = rolService.getAll();
        return ResponseEntity.ok(roles);
    }

    // ====================== DELETE (borrado lógico) =======================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        rolService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/hard/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        rolService.delete(id);
    }
}
