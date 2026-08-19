package com.inventario.pasantia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.pasantia.dto.MensajeResponseDTO;
import com.inventario.pasantia.dto.ProductoRequestDTO;
import com.inventario.pasantia.service.PasantiaProductoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class PasantiaProductoController {

    private final PasantiaProductoService productoService;

    @PostMapping
    public ResponseEntity<MensajeResponseDTO> crearProducto(@RequestBody ProductoRequestDTO request) {
        MensajeResponseDTO response = productoService.crearProducto(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<MensajeResponseDTO> actualizarProducto(@RequestBody ProductoRequestDTO request) {
        MensajeResponseDTO response = productoService.actualizarProducto(request);
        return ResponseEntity.ok(response);
    }
}
