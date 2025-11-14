package com.coagronet.ingredientePresentacionProducto.controllers;

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

import com.coagronet.ingredientePresentacionProducto.dtos.IngredientePresentacionProductoRequestDTO;
import com.coagronet.ingredientePresentacionProducto.dtos.IngredientePresentacionProductoResponseDTO;
import com.coagronet.ingredientePresentacionProducto.services.IngredientePresentacionProductoService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ingrediente-presentacion-producto")
@RequiredArgsConstructor
public class IngredientePresentacionProductoController {

	private final IngredientePresentacionProductoService ingredientePresentacionProductoService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<IngredientePresentacionProductoResponseDTO>> findAll(
			@PageableDefault Pageable pageable) {
		return ResponseEntity.ok(ingredientePresentacionProductoService
				.listarPorEmpresa(pageable));
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<IngredientePresentacionProductoResponseDTO> findById(@PathVariable Long requestedId) {
		return ingredientePresentacionProductoService.findById(requestedId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createIngredientePresentacionProducto(
			@Valid @RequestBody IngredientePresentacionProductoRequestDTO dto,
			UriComponentsBuilder ucb) {
		return ResponseEntity
				.created(uriBuilderUtil.buildIngredientePresentacionProductoUri(
						(ingredientePresentacionProductoService.create(dto)).getId(),
						ucb))
				.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateIngredientePresentacionProducto(@PathVariable Long requestedId,
			@Valid @RequestBody IngredientePresentacionProductoRequestDTO dto) {
		ingredientePresentacionProductoService.update(requestedId, dto);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteIngredientePresentacionProducto(@PathVariable Long id) {
		ingredientePresentacionProductoService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
