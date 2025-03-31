package com.coagronet.departamento.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.departamento.dtos.DepartamentoDTO;
import com.coagronet.departamento.services.DepartamentoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/departamento")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    @GetMapping("/all")
    public ResponseEntity<List<DepartamentoDTO>> findAll() {
        return ResponseEntity.ok(departamentoService.findAll());
    }
}
