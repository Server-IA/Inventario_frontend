package com.coagronet.departamento.controllers;

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

import com.coagronet.departamento.dtos.DepartamentoDTO;
import com.coagronet.departamento.services.DepartamentoService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/departamento")
@RequiredArgsConstructor
public class DepartamentoController {

	private final DepartamentoService departamentoService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<DepartamentoDTO>> findAll() {
		return ResponseEntity.ok(departamentoService.findAll());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<DepartamentoDTO> findById(@PathVariable Long requestedId) {
		return departamentoService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createDepartamento(@Valid @RequestBody DepartamentoDTO departamentoDTO,
			UriComponentsBuilder ucb) {
		DepartamentoDTO savedDepartamento = departamentoService.create(departamentoDTO);
		URI locationOfNewDepartamento = uriBuilderUtil.buildDepartamentoUri(savedDepartamento.getId(), ucb);
		return ResponseEntity.created(locationOfNewDepartamento).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateDepartamento(@PathVariable Long requestedId,
			@Valid @RequestBody DepartamentoDTO departamentoDTO) {
		departamentoService.update(requestedId, departamentoDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteDepartamento(@PathVariable Long id) {
		departamentoService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
