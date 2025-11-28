package com.coagronet.usuariorol.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.usuariorol.dtos.UsuarioRolRequestDTO;
import com.coagronet.usuariorol.dtos.UsuarioRolResponseDTO;
import com.coagronet.usuariorol.services.UsuarioRolService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
public class UsuarioRolController {

    private final UsuarioRolService usuarioRolService;

    @GetMapping("/api/v1/admin/usuario-roles")
    public ResponseEntity<Page<UsuarioRolResponseDTO>> getAll(Pageable pageable) {
        Page<UsuarioRolResponseDTO> page = usuarioRolService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/api/v1/admin/usuario-roles/{id}")
    public ResponseEntity<UsuarioRolResponseDTO> getById(@PathVariable Long id) {
        UsuarioRolResponseDTO response = usuarioRolService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/v1/admin/usuario-roles")
    public ResponseEntity<Void> create(
            @Valid @RequestBody UsuarioRolRequestDTO requestDTO,
            HttpServletRequest httpRequest) {
        UsuarioRolResponseDTO created = usuarioRolService.create(requestDTO, httpRequest);
        URI location = URI.create(String.format("/api/v1/admin/usuario-roles/%d", created.id()));
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/api/v1/admin/usuario-roles/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRolRequestDTO requestDTO,
            HttpServletRequest httpRequest) {
        usuarioRolService.update(id, requestDTO, httpRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/v1/admin/usuario-roles/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioRolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
