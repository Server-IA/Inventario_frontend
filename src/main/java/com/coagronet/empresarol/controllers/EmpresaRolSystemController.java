package com.coagronet.empresarol.controllers;

import com.coagronet.empresarol.dtos.requests.EmpresaRolSystemCreateRequestDTO;
import com.coagronet.empresarol.dtos.requests.EmpresaRolSystemUpdateRequestDTO;
import com.coagronet.empresarol.dtos.responses.EmpresaRolResponseDTO;
import com.coagronet.empresarol.services.EmpresaRolSystemService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador de empresa-rol para el administrador del sistema
 * <p>
 *
 *
 * @author Ángel David Oliveros Yatte
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/system/empresa-rol")
@RequiredArgsConstructor
public class EmpresaRolSystemController {

    private final EmpresaRolSystemService empresaRolSystemService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<EmpresaRolResponseDTO>>findAll(){
        return ResponseEntity.ok(empresaRolSystemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaRolResponseDTO>findById(@PathVariable Long id){
        return ResponseEntity.ok(empresaRolSystemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void>create(@Valid @RequestBody EmpresaRolSystemCreateRequestDTO dto,
                                      UriComponentsBuilder ucb){

        EmpresaRolResponseDTO created = empresaRolSystemService.create(dto);
        URI location = uriBuilderUtil.buildEmpresaRolSystemUri(created.getId(), ucb);
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid EmpresaRolSystemUpdateRequestDTO dto){
        empresaRolSystemService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        empresaRolSystemService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
