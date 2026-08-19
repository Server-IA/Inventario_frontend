package com.inventario.ingredientePresentacionProducto.controllers;

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

import com.inventario.ingredientePresentacionProducto.dtos.IngredientePresentacionProductoRequestDTO;
import com.inventario.ingredientePresentacionProducto.dtos.IngredientePresentacionProductoResponseDTO;
import com.inventario.ingredientePresentacionProducto.services.IngredientePresentacionProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ingrediente-presentacion-producto")
@RequiredArgsConstructor
public class IngredientePresentacionProductoController {

	private final IngredientePresentacionProductoService ingredientePresentacionProductoService;

	// LIST paginado
	@GetMapping
	public ResponseEntity<Page<IngredientePresentacionProductoResponseDTO>> list(
			@PageableDefault(size = 20, sort = "id") Pageable pageable) {

		Page<IngredientePresentacionProductoResponseDTO> page = ingredientePresentacionProductoService.list(pageable);

		return ResponseEntity.ok(page);
	}

	// GET by id
	@GetMapping("/{id}")
	public ResponseEntity<IngredientePresentacionProductoResponseDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(ingredientePresentacionProductoService.getById(id));
	}

	// CREATE
	@PostMapping
	public ResponseEntity<IngredientePresentacionProductoResponseDTO> createIngredientePresentacionProducto(
			@Valid @RequestBody IngredientePresentacionProductoRequestDTO dto,
			UriComponentsBuilder uriBuilder) {

		IngredientePresentacionProductoResponseDTO created = ingredientePresentacionProductoService.create(dto);

		var uri = uriBuilder
				.path("/api/v1/ingrediente-presentacion-producto/{id}")
				.buildAndExpand(created.idIngredientePresentacionProducto())
				.toUri();

		return ResponseEntity.created(uri).body(created);
	}

	// UPDATE
	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateIngredientePresentacionProducto(
			@PathVariable Long requestedId,
			@Valid @RequestBody IngredientePresentacionProductoRequestDTO dto) {

		ingredientePresentacionProductoService.update(requestedId, dto);
		return ResponseEntity.noContent().build();
	}

	// DELETE
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteIngredientePresentacionProducto(@PathVariable Long id) {
		ingredientePresentacionProductoService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
