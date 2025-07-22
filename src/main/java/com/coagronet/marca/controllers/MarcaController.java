package com.coagronet.marca.controllers;

import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.marca.dtos.MarcaDTO;
import com.coagronet.marca.services.MarcaService;

@RestController
@RequestMapping("/api/v1/marca")
@RequiredArgsConstructor
public class MarcaController {

	private final MarcaService marcaService;
	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<MarcaDTO>> findAll() {
		return ResponseEntity.ok(marcaService.findAll());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<MarcaDTO> findById(@PathVariable Long requestedId) {
		return marcaService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createMarca(@Valid @RequestBody MarcaDTO marcaDTO, UriComponentsBuilder ucb) {
		MarcaDTO savedMarca = marcaService.create(marcaDTO);
		return ResponseEntity.created(uriBuilderUtil.buildMarcaUri(savedMarca.getId(), ucb)).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateMarca(@PathVariable Long requestedId, @Valid @RequestBody MarcaDTO marcaDTO) {
		marcaService.update(requestedId, marcaDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMarca(@PathVariable Long id) {
		marcaService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
