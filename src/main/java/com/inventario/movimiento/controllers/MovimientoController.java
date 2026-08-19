package com.inventario.movimiento.controllers;

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

import com.inventario.movimiento.dtos.MovimientoDTO;
import com.inventario.movimiento.services.MovimientoService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/movimiento")
@RequiredArgsConstructor
public class MovimientoController {

	private final MovimientoService movimientoService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<MovimientoDTO>> findAll() {
		return ResponseEntity.ok(movimientoService.findAll());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<MovimientoDTO> findById(@PathVariable Long requestedId) {
		return movimientoService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createMovimiento(@Valid @RequestBody MovimientoDTO movimientoDTO,
			UriComponentsBuilder ucb) {
		return ResponseEntity
			.created(uriBuilderUtil.buildMovimientoUri((movimientoService.create(movimientoDTO)).getId(), ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateMovimiento(@PathVariable Long requestedId,
			@Valid @RequestBody MovimientoDTO movimientoDTO) {
		movimientoService.update(requestedId, movimientoDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMovimiento(@PathVariable Long id) {
		movimientoService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
