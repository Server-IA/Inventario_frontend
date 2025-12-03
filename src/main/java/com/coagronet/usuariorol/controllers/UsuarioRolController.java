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
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/api/v1")
public class UsuarioRolController {

    private final UsuarioRolService usuarioRolService;

    /* ===================== SCOPE SISTEMA (ADMIN GLOBAL) ===================== */

    // Lista TODAS las asignaciones usuario-rol (todas las empresas)
    @GetMapping("/system/usuario-roles")
    public ResponseEntity<Page<UsuarioRolResponseDTO>> getAll(Pageable pageable) {
        Page<UsuarioRolResponseDTO> page = usuarioRolService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    // Obtiene una asignación por id (scope global)
    @GetMapping("/system/usuario-roles/{id}")
    public ResponseEntity<UsuarioRolResponseDTO> getById(@PathVariable Long id) {
        UsuarioRolResponseDTO response = usuarioRolService.findById(id);
        return ResponseEntity.ok(response);
    }

    // Crea una asignación usuario-rol (admin del sistema)
    @PostMapping("/system/usuario-roles")
    public ResponseEntity<Void> create(
            @Valid @RequestBody UsuarioRolRequestDTO requestDTO,
            HttpServletRequest httpRequest) {

        UsuarioRolResponseDTO created = usuarioRolService.create(requestDTO, httpRequest);
        URI location = URI.create(String.format("/api/v1/system/usuario-roles/%d", created.id()));
        return ResponseEntity.created(location).build();
    }

    // Actualiza una asignación usuario-rol (admin del sistema)
    @PutMapping("/system/usuario-roles/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRolRequestDTO requestDTO,
            HttpServletRequest httpRequest) {

        usuarioRolService.update(id, requestDTO, httpRequest);
        return ResponseEntity.noContent().build();
    }

    // Elimina / desactiva una asignación usuario-rol (admin del sistema)
    @DeleteMapping("/system/usuario-roles/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioRolService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /*
     * ===================== SCOPE EMPRESA (EMPRESA DEL TOKEN) =====================
     */

    // Lista asignaciones usuario-rol SOLO de la empresa del token
    @GetMapping("/usuario-roles")
    public ResponseEntity<Page<UsuarioRolResponseDTO>> getAllByEmpresa(Pageable pageable) {
        Page<UsuarioRolResponseDTO> page = usuarioRolService.findAllByEmpresaId(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/usuario-roles/{id}")
    public ResponseEntity<UsuarioRolResponseDTO> getByIdAndEmpresa(@PathVariable Long id) {
        UsuarioRolResponseDTO response = usuarioRolService.findByIdAndEmpresaId(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/usuario-roles/{id}")
    public ResponseEntity<Void> deleteByEmpresa(@PathVariable Long id) {
        usuarioRolService.deleteByEmpresaId(id);
        return ResponseEntity.noContent().build();
    }
}
