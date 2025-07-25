package com.coagronet.articuloInventario.controllers;

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

import com.coagronet.articuloInventario.dtos.ArticuloInventarioDTO;
import com.coagronet.articuloInventario.services.ArticuloInventarioService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/articulo-inventario")
@RequiredArgsConstructor
public class ArticuloInventarioController {

	private final ArticuloInventarioService articuloInventarioService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<ArticuloInventarioDTO>> findAll() {
		return ResponseEntity.ok(articuloInventarioService.findAll());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<ArticuloInventarioDTO> findById(@PathVariable Long requestedId) {
		return articuloInventarioService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createArticuloInventario(
			@Valid @RequestBody ArticuloInventarioDTO articuloInventarioDTO, UriComponentsBuilder ucb) {
		return ResponseEntity
			.created(uriBuilderUtil
				.buildArticuloInventarioUri((articuloInventarioService.create(articuloInventarioDTO)).getId(), ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateArticuloInventario(@PathVariable Long requestedId,
			@Valid @RequestBody ArticuloInventarioDTO articuloInventarioDTO) {
		articuloInventarioService.update(requestedId, articuloInventarioDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteArticuloInventario(@PathVariable Long id) {
		articuloInventarioService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
