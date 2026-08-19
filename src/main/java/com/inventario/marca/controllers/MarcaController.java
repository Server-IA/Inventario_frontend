package com.inventario.marca.controllers;

import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.inventario.marca.dtos.MarcaDTO;
import com.inventario.marca.services.MarcaService;

@RestController
@RequestMapping("/api/v1/marca")
@RequiredArgsConstructor
public class MarcaController {

	private final MarcaService marcaService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<MarcaDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<MarcaDTO> page = marcaService.findAll(pageable);
		if(page.isEmpty()){
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(page);
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
