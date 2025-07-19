package com.coagronet.produccionFase.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.produccionFase.dtos.ProduccionFaseDTO;
import com.coagronet.produccionFase.services.ProduccionFaseService;
import com.coagronet.utils.UriBuilderUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/produccion_fase")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProduccionFaseController {

    private final ProduccionFaseService produccionFaseService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<ProduccionFaseDTO>> findAll() {
        return ResponseEntity.ok(produccionFaseService.findAll());
    }

    @GetMapping("/{requestedId}") 
    
    
}
