package com.inventario.categoriaestado.controllers;

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

import com.inventario.categoriaestado.CategoriaEstado;
import com.inventario.categoriaestado.services.CategoriaEstadoService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categoria-estado")
@RequiredArgsConstructor
public class CategoriaEstadoController {

	private final CategoriaEstadoService service;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<CategoriaEstado>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoriaEstado> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findByIdOrThrow(id));
	}

	@PostMapping
	public ResponseEntity<Void> create(@Valid @RequestBody CategoriaEstado entity, UriComponentsBuilder ucb) {
		return ResponseEntity.created(uriBuilderUtil.buildCategoriaEstadoUri(service.create(entity).getId(), ucb))
			.build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> put(@PathVariable Long id, @Valid @RequestBody CategoriaEstado entity) {
		service.update(id, entity);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
