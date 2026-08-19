package com.inventario.grupo.controllers;

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

import com.inventario.grupo.dtos.GrupoDTO;
import com.inventario.grupo.services.GrupoService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/grupo")
@RequiredArgsConstructor
public class GrupoController {

	private final GrupoService grupoService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<GrupoDTO>> findAll() {
		return ResponseEntity.ok(grupoService.findAll());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<GrupoDTO> findById(@PathVariable Long requestedId) {
		return grupoService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createGrupo(@Valid @RequestBody GrupoDTO grupoDTO, UriComponentsBuilder ucb) {
		GrupoDTO savedGrupo = grupoService.create(grupoDTO);
		URI locationOfNewGrupo = uriBuilderUtil.buildGrupoUri(savedGrupo.getId(), ucb);
		return ResponseEntity.created(locationOfNewGrupo).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateGrupo(@PathVariable Long requestedId, @Valid @RequestBody GrupoDTO grupoDTO) {
		grupoService.update(requestedId, grupoDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteGrupo(@PathVariable Long id) {
		grupoService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
