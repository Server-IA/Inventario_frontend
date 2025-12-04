package com.coagronet.empresarol.controllers;

import com.coagronet.empresarol.dtos.EmpresaRolRequestDTO;
import com.coagronet.empresarol.dtos.EmpresaRolResponseDTO;
import com.coagronet.empresarol.services.EmpresaRolService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<Void> create(@RequestBody @Valid EmpresaRolRequestDTO dto, UriComponentsBuilder ucb){

        EmpresaRolResponseDTO created = empresaRolService.create(dto);
        URI location = uriBuilderUtil.buildEmpresaRolUri(created.getId(), ucb);

        return ResponseEntity.created(location).build();
    }
}
