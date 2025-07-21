package com.coagronet.espacio.controllers;

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

import com.coagronet.espacio.dtos.EspacioDTO;
import com.coagronet.espacio.services.EspacioService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/espacio")
@RequiredArgsConstructor
public class EspacioController {

	private final EspacioService espacioService;
	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<EspacioDTO>> findAll() {
		return ResponseEntity.ok(espacioService.findAll());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<EspacioDTO> findById(@PathVariable Long requestedId) {
		return espacioService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createEspacio(@Valid @RequestBody EspacioDTO espacioDTO, UriComponentsBuilder ucb) {
		EspacioDTO savedEspacio = espacioService.create(espacioDTO);
		URI locationOfNewEspacio = uriBuilderUtil.buildEspacioUri(savedEspacio.getId(), ucb);
		return ResponseEntity.created(locationOfNewEspacio).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateEspacio(@PathVariable Long requestedId,
			@Valid @RequestBody EspacioDTO espacioDTO) {
		espacioService.update(requestedId, espacioDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEspacio(@PathVariable Long id) {
		espacioService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
