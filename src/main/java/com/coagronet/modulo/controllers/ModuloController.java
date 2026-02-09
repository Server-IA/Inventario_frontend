package com.coagronet.modulo.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.modulo.dtos.ModuloRequest;
import com.coagronet.modulo.services.ModuloService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/modulos")
public class ModuloController {

    private final ModuloService moduloService;

    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    @PostMapping
    public ResponseEntity<Void> crear(@Valid @RequestBody ModuloRequest request) {
        Long id = moduloService.crearModulo(request);
        return ResponseEntity.created(URI.create("/api/v1/modulos/" + id)).build();
    }
}
