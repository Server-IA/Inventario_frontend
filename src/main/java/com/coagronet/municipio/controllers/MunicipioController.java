package com.coagronet.municipio.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.municipio.dtos.MunicipioDTO;
import com.coagronet.municipio.services.MunicipioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/municipio")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MunicipioController {

    private final MunicipioService municipioService;

    @GetMapping("/all")
    public ResponseEntity<List<MunicipioDTO>> findAll() {
        return ResponseEntity.ok(municipioService.findAll());
    }
}