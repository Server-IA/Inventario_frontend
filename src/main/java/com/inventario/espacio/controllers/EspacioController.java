package com.inventario.espacio.controllers;

import java.net.URI;

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

import com.inventario.espacio.dtos.EspacioDTO;
import com.inventario.espacio.services.EspacioService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/espacio")
@RequiredArgsConstructor
public class EspacioController {

	private final EspacioService espacioService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<EspacioDTO>> findAll(@PageableDefault Pageable pageable) {

		Page<EspacioDTO> page = espacioService.findAll(pageable);

		if(page.isEmpty()){
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(page);
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
