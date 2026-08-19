package com.inventario.bloque.controllers;

import java.net.URI;

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

import com.inventario.bloque.dtos.BloqueDTO;
import com.inventario.bloque.services.BloqueService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bloque")
@RequiredArgsConstructor
public class BloqueController {

	private final BloqueService bloqueService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<BloqueDTO>> findAll(@PageableDefault() Pageable pageable) {
		Page<BloqueDTO> page = bloqueService.findAll(pageable);

		if (page.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(page);
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<BloqueDTO> findById(@PathVariable Long requestedId) {
		return bloqueService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createBloque(@Valid @RequestBody BloqueDTO bloqueDTO, UriComponentsBuilder ucb) {
		BloqueDTO savedBloque = bloqueService.create(bloqueDTO);
		URI locationOfNewBloque = uriBuilderUtil.buildBloqueUri(savedBloque.getId(), ucb);
		return ResponseEntity.created(locationOfNewBloque).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateBloque(@PathVariable Long requestedId, @Valid @RequestBody BloqueDTO bloqueDTO) {
		bloqueService.update(requestedId, bloqueDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBloque(@PathVariable Long id) {
		bloqueService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
