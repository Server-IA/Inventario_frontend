package com.coagronet.articuloOrdenCompra.controllers;

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

import com.coagronet.articuloOrdenCompra.dtos.ArticuloOrdenCompraDTO;
import com.coagronet.articuloOrdenCompra.services.ArticuloOrdenCompraService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/articulo-orden-compra")
@RequiredArgsConstructor
public class ArticuloOrdenCompraController {

	private final ArticuloOrdenCompraService articuloOrdenCompraService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<ArticuloOrdenCompraDTO>> findAll() {
		return ResponseEntity.ok(articuloOrdenCompraService.findAll());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<ArticuloOrdenCompraDTO> findById(@PathVariable Long requestedId) {
		return articuloOrdenCompraService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createArticuloOrdenCompra(
			@Valid @RequestBody ArticuloOrdenCompraDTO articuloOrdenCompraDTO, UriComponentsBuilder ucb) {
		return ResponseEntity
			.created(uriBuilderUtil
				.buildArticuloOrdenCompraUri((articuloOrdenCompraService.create(articuloOrdenCompraDTO)).getId(), ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateArticuloOrdenCompra(@PathVariable Long requestedId,
			@Valid @RequestBody ArticuloOrdenCompraDTO articuloOrdenCompraDTO) {
		articuloOrdenCompraService.update(requestedId, articuloOrdenCompraDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteArticuloOrdenCompra(@PathVariable Long id) {
		articuloOrdenCompraService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
