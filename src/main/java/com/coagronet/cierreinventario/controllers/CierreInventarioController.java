package com.coagronet.cierreinventario.controllers;

import com.coagronet.cierreinventario.dtos.CierreInventarioRequestDTO;
import com.coagronet.cierreinventario.dtos.CierreInventarioResponseDTO;
import com.coagronet.cierreinventario.services.CierreInventarioService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cierre-inventario")
public class CierreInventarioController {

    private final CierreInventarioService cierreInventarioService;
    private final UriBuilderUtil uriBuilderUtil;


    @GetMapping
    public ResponseEntity<List<CierreInventarioResponseDTO>>findAll(){
        List<CierreInventarioResponseDTO> cierres = cierreInventarioService.listAll();
        return ResponseEntity.ok(cierres);
    }

    @PostMapping
    public ResponseEntity<CierreInventarioResponseDTO> create(@RequestBody @Valid CierreInventarioRequestDTO dto,
                                                              UriComponentsBuilder ucb){
        CierreInventarioResponseDTO savedCierreInventarioDto = cierreInventarioService.create(dto);
        URI locationOfNewCierreInventario = uriBuilderUtil.buildCierreInventarioUri(savedCierreInventarioDto.getId(), ucb);

        return ResponseEntity.created(locationOfNewCierreInventario).build();
    }
}
