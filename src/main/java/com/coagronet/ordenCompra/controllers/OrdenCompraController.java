package com.coagronet.ordenCompra.controllers;

import java.util.List;

import com.coagronet.ordenCompra.services.OrdenCompraService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import com.coagronet.ordenCompra.dtos.OrdenCompraDTO;

@RestController
@RequestMapping("/api/v1/orden_compra")
@RequiredArgsConstructor
public class OrdenCompraController {

	private final OrdenCompraService ordenCompraService;
	private final ArticuloOrdenCompraService articuloOrdenCompraService;
	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<OrdenCompraDTO>> findAll() {
		return ResponseEntity.ok(ordenCompraService.findAll());
	}

	@GetMapping("/{ordenCompraId}/articulos")
	public ResponseEntity<List<ArticuloOrdenCompraDTO>> findArticulosByOrdenCompra(
			@PathVariable Long ordenCompraId) {
		return ResponseEntity.ok(articuloOrdenCompraService.findAllByOrdenCompraId(ordenCompraId));
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<OrdenCompraDTO> findById(@PathVariable Long requestedId) {
		return ordenCompraService.findById(requestedId).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createOrdenCompra(@Valid @RequestBody OrdenCompraDTO ordenCompraDTO,
			UriComponentsBuilder ucb) {
		return ResponseEntity
				.created(uriBuilderUtil
						.buildOrdenCompraUri((ordenCompraService.create(ordenCompraDTO)).getId(), ucb))
				.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateOrdenCompra(@PathVariable Long requestedId,
			@Valid @RequestBody OrdenCompraDTO ordenCompraDTO) {
		ordenCompraService.update(requestedId, ordenCompraDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOrdenCompra(@PathVariable Long id) {
		ordenCompraService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
