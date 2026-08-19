package com.inventario.cierreinventariodetalle.controllers;

import com.inventario.cierreinventariodetalle.dtos.CierreInventarioDetalleResponseDTO;
import com.inventario.cierreinventariodetalle.services.CierreInventarioDetalleService;
import com.inventario.utils.UriBuilderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cierre-inventario-detalle")
public class CierreInventarioDetalleController {

    private final CierreInventarioDetalleService cierreInventarioDetalleService;
    private final UriBuilderUtil uriBuilderUtil;


    @GetMapping
    public ResponseEntity<Page<CierreInventarioDetalleResponseDTO>> findAll(@PageableDefault Pageable pageable){
        Page<CierreInventarioDetalleResponseDTO> detalles = cierreInventarioDetalleService.findAll(pageable);
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{cierreId}")
    public ResponseEntity<CierreInventarioDetalleResponseDTO>findById(@PathVariable Long cierreId){
        return ResponseEntity.ok(cierreInventarioDetalleService.findById(cierreId));
    }


}
