package com.coagronet.pais.controllers;

import java.net.URI;
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

import com.coagronet.pais.dtos.PaisDTO;
import com.coagronet.pais.services.PaisService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pais")
@RequiredArgsConstructor
public class PaisController {

	private final PaisService paisService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<PaisDTO>> findAll() {
		return ResponseEntity.ok(paisService.findAll());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<PaisDTO> findById(@PathVariable Long requestedId) {
		return paisService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createPais(@Valid @RequestBody PaisDTO paisDTO, UriComponentsBuilder ucb) {
		PaisDTO savedPais = paisService.create(paisDTO);
		URI locationOfNewPais = uriBuilderUtil.buildPaisUri(savedPais.getId(), ucb);
		return ResponseEntity.created(locationOfNewPais).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updatePais(@PathVariable Long requestedId, @Valid @RequestBody PaisDTO paisDTO) {
		paisService.update(requestedId, paisDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePais(@PathVariable Long id) {
		paisService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
