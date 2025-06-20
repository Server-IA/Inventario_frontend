package com.coagronet.seccion.controllers;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.seccion.dtos.SeccionDTO;
import com.coagronet.seccion.services.SeccionService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seccion")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SeccionController {

    private final SeccionService seccionService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<SeccionDTO>> findAll() {
        return ResponseEntity.ok(seccionService.findAll()); // 200 OK
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<SeccionDTO> findById(@PathVariable Long requestedId) {
        return ResponseEntity.ok(
                seccionService.findById(requestedId)
                        .orElseThrow(() -> new NotFoundException("La sección no fue encontrada"))
        );
    }

    @PostMapping
    public ResponseEntity<Void> createSeccion(@Valid @RequestBody SeccionDTO seccionDTO, UriComponentsBuilder ucb) {
        Long newId = seccionService.create(seccionDTO).getId();
        return ResponseEntity.created(uriBuilderUtil.buildSeccionUri(newId, ucb)).build(); // 201 Created
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateSeccion(@PathVariable Long requestedId,
                                              @Valid @RequestBody SeccionDTO seccionDTO) {
        seccionService.update(requestedId, seccionDTO);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeccion(@PathVariable Long id) {
        seccionService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}