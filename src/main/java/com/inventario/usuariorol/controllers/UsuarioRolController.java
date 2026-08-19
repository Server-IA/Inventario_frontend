package com.inventario.usuariorol.controllers;

import java.net.URI;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.inventario.usuariorol.dtos.UsuarioRolRequestDTO;
import com.inventario.usuariorol.dtos.UsuarioRolRequestForCurrentEmpresaDTO;
import com.inventario.usuariorol.dtos.UsuarioRolResponseDTO;
import com.inventario.usuariorol.dtos.UsuarioRolResponseForCurrentEmpresaDTO;
import com.inventario.usuariorol.services.UsuarioRolService;

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

    @Operation(
            summary = "Listar usuario-roles (scope sistema)",
            description = "Obtiene todas las asignaciones usuario-rol de todas las empresas. Requiere rol ADMINISTRADOR_SISTEMA"
    )
    @GetMapping("/system/usuario-roles")
    public ResponseEntity<Page<UsuarioRolResponseDTO>> findAll(Pageable pageable) {
        Page<UsuarioRolResponseDTO> page = usuarioRolService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Obtener usuario-rol por ID (scope sistema)",
            description = "Obtiene una asignación usuario-rol específica a nivel global (todas las empresas)"
    )
    @GetMapping("/system/usuario-roles/{id}")
    public ResponseEntity<UsuarioRolResponseDTO> findById(@PathVariable Long id) {
        UsuarioRolResponseDTO response = usuarioRolService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Crear usuario-rol (scope sistema)",
            description = "Crea una asignación usuario-rol para cualquier empresa. Requiere rol ADMINISTRADOR_SISTEMA"
    )
    @PostMapping("/system/usuario-roles")
    public ResponseEntity<Void> create(
            @Valid @RequestBody UsuarioRolRequestDTO requestDTO,
            HttpServletRequest httpRequest) {

        UsuarioRolResponseDTO created = usuarioRolService.create(requestDTO, httpRequest);
        URI location = URI.create(String.format("/api/v1/system/usuario-roles/%d", created.id()));
        return ResponseEntity.created(location).build();
    }

    @Operation(
            summary = "Actualizar usuario-rol (scope sistema)",
            description = "Actualiza una asignación usuario-rol existente en cualquier empresa"
    )
    @PutMapping("/system/usuario-roles/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRolRequestDTO requestDTO,
            HttpServletRequest httpRequest) {

        usuarioRolService.update(id, requestDTO, httpRequest);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Eliminar usuario-rol (scope sistema)",
            description = "Elimina o desactiva una asignación usuario-rol de cualquier empresa"
    )
    @DeleteMapping("/system/usuario-roles/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioRolService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Cambiar estado usuario-rol (scope sistema)",
            description = "Activa o desactiva una asignación usuario-rol en cualquier empresa (toggle de estado)"
    )
    @PatchMapping("/system/usuario-roles/{id}/toggle-estado")
    @PreAuthorize("hasRole('ADMINISTRADOR_SISTEMA')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> toggleEstado(
            @PathVariable Long id,
            @RequestParam(name = "empresaId") Long empresaId) {
        usuarioRolService.toggleEstado(id, empresaId);
        return ResponseEntity.ok().build();
    }

    /*
     * ===================== SCOPE EMPRESA =====================
     */

    @Operation(
            summary = "Listar usuario-roles (empresa actual)",
            description = "Obtiene las asignaciones usuario-rol de la empresa asociada al token JWT"
    )
    @GetMapping("/usuario-roles")
    public ResponseEntity<Page<UsuarioRolResponseForCurrentEmpresaDTO>> findAllForCurrentEmpresa(Pageable pageable) {
        Page<UsuarioRolResponseForCurrentEmpresaDTO> page = usuarioRolService.findAllForCurrentEmpresa(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Obtener usuario-rol por ID (empresa actual)",
            description = "Obtiene una asignación usuario-rol dentro de la empresa del token"
    )
    @GetMapping("/usuario-roles/{id}")
    public ResponseEntity<UsuarioRolResponseForCurrentEmpresaDTO> findByIdForCurrentEmpresa(@PathVariable Long id) {
        UsuarioRolResponseForCurrentEmpresaDTO response = usuarioRolService.findByIdForCurrentEmpresa(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Crear usuario-rol (empresa actual)",
            description = "Crea una asignación usuario-rol en la empresa del contexto del token"
    )
    @PostMapping("/usuario-roles")
    public ResponseEntity<Void> createForCurrentEmpresa(
            @Valid @RequestBody UsuarioRolRequestForCurrentEmpresaDTO requestDTO,
            HttpServletRequest httpRequest) {

        UsuarioRolResponseDTO created = usuarioRolService.createForCurrentEmpresa(requestDTO, httpRequest);
        URI location = URI.create(String.format("/api/v1/usuario-roles/%d", created.id()));
        return ResponseEntity.created(location).build();
    }

    @Operation(
            summary = "Actualizar usuario-rol (empresa actual)",
            description = "Actualiza una asignación usuario-rol dentro de la empresa del token"
    )
    @PutMapping("/usuario-roles/{id}")
    public ResponseEntity<Void> updateForCurrentEmpresa(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRolRequestForCurrentEmpresaDTO requestDTO,
            HttpServletRequest httpRequest) {

        usuarioRolService.updateForCurrentEmpresa(id, requestDTO, httpRequest);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Eliminar usuario-rol (empresa actual)",
            description = "Elimina o desactiva una asignación usuario-rol en la empresa actual"
    )
    @DeleteMapping("/usuario-roles/{id}")
    public ResponseEntity<Void> deleteForCurrentEmpresa(@PathVariable Long id) {
        usuarioRolService.deleteForCurrentEmpresa(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Cambiar estado usuario-rol (empresa actual)",
            description = "Activa o desactiva una asignación usuario-rol dentro de la empresa del token (toggle de estado)"
    )
    @PatchMapping("/usuario-roles/{id}/toggle-estado")
    @PreAuthorize("hasAuthority('USUARIO_ROL_INACTIVATE')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> toggleEstadoForCurrentEmpresa(@PathVariable Long id) {
        usuarioRolService.toggleEstadoForCurrentEmpresa(id);
        return ResponseEntity.ok().build();
    }
}