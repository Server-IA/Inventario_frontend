package com.inventario.metricas.controllers;

import com.inventario.metricas.dtos.EmpresaTotalResponseDTO;
import com.inventario.metricas.services.EmpresaContadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metricas")
@RequiredArgsConstructor
public class EmpresaContadorController {
    private final EmpresaContadorService empresaContadorService;


    @GetMapping("/contar")
    public ResponseEntity<EmpresaTotalResponseDTO> getEntityCounts(
            @RequestParam(required = false) List<String> entidades) {

        EmpresaTotalResponseDTO response = empresaContadorService.contarEntidades(entidades);
        return ResponseEntity.ok(response);
    }
}
