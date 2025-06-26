package com.coagronet.bloque.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.bloque.dtos.BloqueDTO;
import com.coagronet.bloque.services.BloqueService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bloque")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BloqueController {

	private final BloqueService bloqueService;
	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<BloqueDTO>> findAll() {
		return ResponseEntity.ok(bloqueService.findAll());
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
