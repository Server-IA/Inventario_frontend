package com.inventario.ingrediente.controllers;


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

import com.inventario.ingrediente.dtos.IngredienteDTO;
import com.inventario.ingrediente.services.IngredienteService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ingrediente")
@RequiredArgsConstructor
public class IngredienteController {

	private final IngredienteService ingredienteService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<IngredienteDTO>> findAll(@PageableDefault Pageable pageable) {

		Page<IngredienteDTO> page = ingredienteService.findAll(pageable);

		if(page.isEmpty()){
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(page);
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<IngredienteDTO> findById(@PathVariable Long requestedId) {
		return ingredienteService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createIngrediente(@Valid @RequestBody IngredienteDTO ingredienteDTO,
			UriComponentsBuilder ucb) {
		IngredienteDTO savedIngrediente = ingredienteService.create(ingredienteDTO);
		return ResponseEntity.created(uriBuilderUtil.buildIngredienteUri(savedIngrediente.getId(), ucb)).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateIngrediente(@PathVariable Long requestedId,
			@Valid @RequestBody IngredienteDTO ingredienteDTO) {
		ingredienteService.update(requestedId, ingredienteDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteIngrediente(@PathVariable Long id) {
		ingredienteService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
