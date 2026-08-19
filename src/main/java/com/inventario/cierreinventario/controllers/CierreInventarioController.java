package com.inventario.cierreinventario.controllers;

import com.inventario.cierreinventario.dtos.CierreInventarioRequestDTO;
import com.inventario.cierreinventario.dtos.CierreInventarioResponseDTO;
import com.inventario.cierreinventario.services.CierreInventarioService;
import com.inventario.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cierre-inventario")
public class CierreInventarioController {

    private final CierreInventarioService cierreInventarioService;
    private final UriBuilderUtil uriBuilderUtil;


    @GetMapping
    public ResponseEntity<Page<CierreInventarioResponseDTO>>findAll(@PageableDefault Pageable pageable){
        Page<CierreInventarioResponseDTO> cierres = cierreInventarioService.findAll(pageable);
        return ResponseEntity.ok(cierres);
    }

    @GetMapping("/{cierreId}")
    public ResponseEntity<CierreInventarioResponseDTO>findById(@PathVariable Long cierreId){
        return ResponseEntity.ok(cierreInventarioService.findById(cierreId));
    }

    @PostMapping
    public ResponseEntity<CierreInventarioResponseDTO> create(@RequestBody @Valid CierreInventarioRequestDTO dto,
                                                              UriComponentsBuilder ucb){
        CierreInventarioResponseDTO savedCierreInventarioDto = cierreInventarioService.create(dto);
        URI locationOfNewCierreInventario = uriBuilderUtil.buildCierreInventarioUri(savedCierreInventarioDto.getId(), ucb);

        return ResponseEntity.created(locationOfNewCierreInventario).build();
    }
}
