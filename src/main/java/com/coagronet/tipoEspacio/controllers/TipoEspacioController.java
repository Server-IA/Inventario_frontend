package com.coagronet.tipoEspacio.controllers;

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

import com.coagronet.tipoEspacio.dtos.TipoEspacioDTO;
import com.coagronet.tipoEspacio.services.TipoEspacioService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tipo_espacio")
@RequiredArgsConstructor
public class TipoEspacioController {

	private final TipoEspacioService tipoEspacioService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<TipoEspacioDTO>> findAll() {
		return ResponseEntity.ok(tipoEspacioService.findAll());
	}

	@GetMapping(params = "available=true")
	public ResponseEntity<List<TipoEspacioDTO>> findAllAvailable() {
		return ResponseEntity.ok(tipoEspacioService.findAllAvailable());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<TipoEspacioDTO> findById(@PathVariable Long requestedId) {
		return tipoEspacioService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createTipoEspacio(@Valid @RequestBody TipoEspacioDTO tipoEspacioDTO,
			UriComponentsBuilder ucb) {
		TipoEspacioDTO savedTipoEspacio = tipoEspacioService.create(tipoEspacioDTO);
		URI locationOfNewTipoEspacio = uriBuilderUtil.buildTipoEspacioUri(savedTipoEspacio.getId(), ucb);
		return ResponseEntity.created(locationOfNewTipoEspacio).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateTipoEspacio(@PathVariable Long requestedId,
			@Valid @RequestBody TipoEspacioDTO tipoEspacioDTO) {
		tipoEspacioService.update(requestedId, tipoEspacioDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTipoEspacio(@PathVariable Long id) {
		tipoEspacioService.delete(id);
		return ResponseEntity.noContent().build();
	}

}