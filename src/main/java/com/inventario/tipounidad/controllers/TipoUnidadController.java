package com.inventario.tipounidad.controllers;

import com.inventario.tipounidad.dtos.TipoUnidadDTO;
import com.inventario.tipounidad.services.TipoUnidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tipo-unidad")
@RequiredArgsConstructor
public class TipoUnidadController {

	private final TipoUnidadService tipoUnidadService;

	@GetMapping("/{requestedId}")
	public ResponseEntity<TipoUnidadDTO> findById(@PathVariable Long requestedId) {
		return tipoUnidadService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<List<TipoUnidadDTO>> findAll() {
		List<TipoUnidadDTO> unidadDTOList = tipoUnidadService.findAll();

		return ResponseEntity.ok(unidadDTOList);
	}

	@PostMapping
	public ResponseEntity<Void> createUnidad(@Valid @RequestBody TipoUnidadDTO newUnidadRequest, UriComponentsBuilder ucb) {

		TipoUnidadDTO savedUnidad = tipoUnidadService.create(newUnidadRequest);

		URI locationOfNewUnidad = ucb.path("/{id}").buildAndExpand(savedUnidad.getId()).toUri();
		return ResponseEntity.created(locationOfNewUnidad).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> putUnidad(@PathVariable Long requestedId,
			@Valid @RequestBody TipoUnidadDTO unidadDTOUpdate) {

		tipoUnidadService.update(requestedId, unidadDTOUpdate);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUnidad(@PathVariable Long id) {
		tipoUnidadService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
