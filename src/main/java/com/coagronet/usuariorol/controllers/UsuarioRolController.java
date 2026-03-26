package com.coagronet.usuariorol.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.coagronet.usuariorol.dtos.UsuarioRolRequestDTO;
import com.coagronet.usuariorol.dtos.UsuarioRolRequestForCurrentEmpresaDTO;
import com.coagronet.usuariorol.dtos.UsuarioRolResponseDTO;
import com.coagronet.usuariorol.dtos.UsuarioRolResponseForCurrentEmpresaDTO;
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
    public ResponseEntity<Page<UsuarioRolResponseDTO>> findAll(Pageable pageable) {
        Page<UsuarioRolResponseDTO> page = usuarioRolService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    // Obtiene una asignación por id (scope global)
    @GetMapping("/system/usuario-roles/{id}")
    public ResponseEntity<UsuarioRolResponseDTO> findById(@PathVariable Long id) {
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
    public ResponseEntity<Page<UsuarioRolResponseForCurrentEmpresaDTO>> findAllForCurrentEmpresa(Pageable pageable) {
        Page<UsuarioRolResponseForCurrentEmpresaDTO> page = usuarioRolService.findAllForCurrentEmpresa(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/usuario-roles/{id}")
    public ResponseEntity<UsuarioRolResponseForCurrentEmpresaDTO> findByIdForCurrentEmpresa(@PathVariable Long id) {
        UsuarioRolResponseForCurrentEmpresaDTO response = usuarioRolService.findByIdForCurrentEmpresa(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/usuario-roles")
    public ResponseEntity<Void> createForCurrentEmpresa(
            @Valid @RequestBody UsuarioRolRequestForCurrentEmpresaDTO requestDTO,
            HttpServletRequest httpRequest) {

        UsuarioRolResponseDTO created = usuarioRolService.createForCurrentEmpresa(requestDTO, httpRequest);
        URI location = URI.create(String.format("/api/v1/usuario-roles/%d", created.id()));
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/usuario-roles/{id}")
    public ResponseEntity<Void> updateForCurrentEmpresa(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRolRequestForCurrentEmpresaDTO requestDTO,
            HttpServletRequest httpRequest) {

        usuarioRolService.updateForCurrentEmpresa(id, requestDTO, httpRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario-roles/{id}")
    public ResponseEntity<Void> deleteForCurrentEmpresa(@PathVariable Long id) {
        usuarioRolService.deleteForCurrentEmpresa(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/usuario-roles/{id}/toggle-estado")
    @PreAuthorize("hasAuthority('USUARIO_ROL_INACTIVATE')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> toggleEstado(
            @PathVariable Long id,
            @RequestParam(name = "empresaId") Long empresaId) {
        usuarioRolService.toggleEstado(id, empresaId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/usuario-roles/{id}/toggle-estado/empresa")
    @PreAuthorize("hasAuthority('USUARIO_ROL_INACTIVATE')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> toggleEstadoForCurrentEmpresa(@PathVariable Long id) {
        usuarioRolService.toggleEstadoForCurrentEmpresa(id);
        return ResponseEntity.ok().build();
    }
}
