package com.coagronet.empresarol.controllers;

import com.coagronet.empresarol.dtos.requests.EmpresaRolCreateRequestDTO;
import com.coagronet.empresarol.dtos.requests.EmpresaRolUpdateRequestDTO;
import com.coagronet.empresarol.dtos.responses.EmpresaRolResponseDTO;
import com.coagronet.empresarol.services.EmpresaRolService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
public class EmpresaRolController {

    private final EmpresaRolService empresaRolService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<EmpresaRolResponseDTO>> findAll(){
        return ResponseEntity.ok(empresaRolService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaRolResponseDTO>findById(@PathVariable Long id){
        return ResponseEntity.ok(empresaRolService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid EmpresaRolCreateRequestDTO dto, UriComponentsBuilder ucb){

        EmpresaRolResponseDTO created = empresaRolService.create(dto);
        URI location = uriBuilderUtil.buildEmpresaRolUri(created.getId(), ucb);

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid EmpresaRolUpdateRequestDTO dto){
        empresaRolService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/updateEstado/{estadoId}")
    public ResponseEntity<Void> updateEstado(@PathVariable Long id, @PathVariable Long estadoId){
        empresaRolService.updateEstado(id, estadoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/toggleEstado/{empresaRolId}")
    public ResponseEntity<Void> toggleEstado(@PathVariable Long empresaRolId){
        empresaRolService.toggleEstadoEmpresaRol(empresaRolId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        empresaRolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
