package com.coagronet.sede.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.sede.dtos.SedeDTO;
import com.coagronet.sede.services.SedeService;
import com.coagronet.utils.UriBuilderUtil;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sede")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SedeController {

    private final SedeService sedeService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping("/all")
    public List<SedeDTO> findAll() {
        return sedeService.findAll();
    }

    @GetMapping("/available")
    public List<SedeDTO> findAllAvailable() {
        return sedeService.findAllAvailable();
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<SedeDTO> findById(@PathVariable Long requestedId) {
        SedeDTO sedeDTO = sedeService.findById(requestedId);
        if (sedeDTO != null) {
            return ResponseEntity.ok(sedeDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> createSede(
            @RequestBody SedeDTO newSedeDTORequest,
            UriComponentsBuilder ucb) {
        SedeDTO savedSede = sedeService.create(newSedeDTORequest);
        URI locationOfNewSede = uriBuilderUtil.buildSedeUri(
                savedSede.getId(),
                ucb);
        return ResponseEntity.created(locationOfNewSede).build();
    }

}
