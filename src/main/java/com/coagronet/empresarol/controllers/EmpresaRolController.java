/*=============================================================================
 Nombre del archivo : EmpresaRolController.java
 Descripcion        : Controlador REST para la gestión de roles a nivel de empresa.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-27 | 0.4.0   | JUAN JOSE CASTRO     | Creación del endpoint GET   |
 |            |         |                      | /select para proveer la     |
 |            |         |                      | lista de roles activos para |
 |            |         |                      | menús de selección.         |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.empresarol.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.empresarol.dtos.requests.EmpresaRolCreateRequestDTO;
import com.coagronet.empresarol.dtos.requests.EmpresaRolUpdateRequestDTO;
import com.coagronet.empresarol.dtos.responses.EmpresaRolResponseDTO;
import com.coagronet.empresarol.dtos.responses.EmpresaRolSelectDTO;
import com.coagronet.empresarol.services.EmpresaRolService;
import com.coagronet.utils.UriBuilderUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador de empresa-rol para el administrador de empresa.
 * <p>
 *
 *
 * @author Ángel David Oliveros Yatte
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/empresa-rol")
@RequiredArgsConstructor
@Tag(name = "Empresa-Rol", description = "Endpoints para la gestión de roles a nivel de empresa por parte del administrador de empresa")
public class EmpresaRolController {

    private final EmpresaRolService empresaRolService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<EmpresaRolResponseDTO>> findAll() {
        return ResponseEntity.ok(empresaRolService.findAll());
    }

    @GetMapping("/select")
    @PreAuthorize("hasAuthority('ROL_READ_ALL') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
    @Operation(summary = "Obtener roles de empresa para listas de selección", description = "Retorna una lista simplificada de roles activos (ID y nombre) para poblar componentes select en el frontend.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<List<EmpresaRolSelectDTO>> getForSelect(
            @RequestParam(required = false) Long empresaId) {
        return ResponseEntity.ok(empresaRolService.getForSelect(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaRolResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(empresaRolService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid EmpresaRolCreateRequestDTO dto, UriComponentsBuilder ucb) {

        EmpresaRolResponseDTO created = empresaRolService.create(dto);
        URI location = uriBuilderUtil.buildEmpresaRolUri(created.getId(), ucb);

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid EmpresaRolUpdateRequestDTO dto) {
        empresaRolService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/updateEstado/{estadoId}")
    public ResponseEntity<Void> updateEstado(@PathVariable Long id, @PathVariable Long estadoId) {
        empresaRolService.updateEstado(id, estadoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/toggleEstado/{empresaRolId}")
    public ResponseEntity<Void> toggleEstado(@PathVariable Long empresaRolId) {
        empresaRolService.toggleEstadoEmpresaRol(empresaRolId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        empresaRolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
