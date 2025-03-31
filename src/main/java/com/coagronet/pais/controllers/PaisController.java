package com.coagronet.pais.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.pais.dtos.PaisDTO;
import com.coagronet.pais.services.PaisService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pais")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaisController {

    private final PaisService paisService;

    @GetMapping("/all")
    public ResponseEntity<List<PaisDTO>> findAll() {
        return ResponseEntity.ok(paisService.findAll());
    }

}
