package com.inventario.tipoIdentificacion.controllers;

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

import com.inventario.tipoIdentificacion.dtos.TipoIdentificacionDTO;
import com.inventario.tipoIdentificacion.services.TipoIdentificacionService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tipo_identificacion")
@RequiredArgsConstructor
public class TipoIdentificacionController {

	private final TipoIdentificacionService tipoIdentificacionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<TipoIdentificacionDTO>> findAll() {
		return ResponseEntity.ok(tipoIdentificacionService.findAll());
	}

	@GetMapping(params = "available=true")
	public ResponseEntity<List<TipoIdentificacionDTO>> findAllAvailable() {
		return ResponseEntity.ok(tipoIdentificacionService.findAllAvailable());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<TipoIdentificacionDTO> findById(@PathVariable Long requestedId) {
		return tipoIdentificacionService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createTipoIdentificacion(
			@Valid @RequestBody TipoIdentificacionDTO tipoIdentificacionDTO, UriComponentsBuilder ucb) {
		TipoIdentificacionDTO savedTipoIdentificacion = tipoIdentificacionService.create(tipoIdentificacionDTO);
		URI locationOfNewTipoIdentificacion = uriBuilderUtil.buildTipoIdentificacionUri(savedTipoIdentificacion.getId(),
				ucb);
		return ResponseEntity.created(locationOfNewTipoIdentificacion).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateTipoIdentificacion(@PathVariable Long requestedId,
			@Valid @RequestBody TipoIdentificacionDTO tipoIdentificacionDTO) {
		tipoIdentificacionService.update(requestedId, tipoIdentificacionDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTipoIdentificacion(@PathVariable Long id) {
		tipoIdentificacionService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
