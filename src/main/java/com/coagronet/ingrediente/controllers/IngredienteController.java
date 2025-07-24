package com.coagronet.ingrediente.controllers;

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

import com.coagronet.ingrediente.dtos.IngredienteDTO;
import com.coagronet.ingrediente.services.IngredienteService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ingrediente")
@RequiredArgsConstructor
public class IngredienteController {

	private final IngredienteService ingredienteService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<IngredienteDTO>> findAll() {
		return ResponseEntity.ok(ingredienteService.findAll());
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
