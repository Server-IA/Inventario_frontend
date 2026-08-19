package com.inventario.tipoProduccion.controllers;

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

import com.inventario.tipoProduccion.dtos.TipoProduccionDTO;
import com.inventario.tipoProduccion.services.TipoProduccionService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tipo_produccion")
@RequiredArgsConstructor
public class TipoProduccionController {

	private final TipoProduccionService tipoProduccionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<TipoProduccionDTO>> findAll() {
		return ResponseEntity.ok(tipoProduccionService.findAll());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<TipoProduccionDTO> findById(@PathVariable Long requestedId) {
		return tipoProduccionService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createTipoProduccion(@Valid @RequestBody TipoProduccionDTO tipoProduccionDTO,
			UriComponentsBuilder ucb) {
		return ResponseEntity
			.created(uriBuilderUtil.buildTipoProduccionUri((tipoProduccionService.create(tipoProduccionDTO)).getId(),
					ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateTipoProduccion(@PathVariable Long requestedId,
			@Valid @RequestBody TipoProduccionDTO tipoProduccionDTO) {
		tipoProduccionService.update(requestedId, tipoProduccionDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTipoProduccion(@PathVariable Long id) {
		tipoProduccionService.delete(id);
		return ResponseEntity.noContent().build();
	}

}