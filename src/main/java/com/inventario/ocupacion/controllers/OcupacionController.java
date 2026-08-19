package com.inventario.ocupacion.controllers;

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

import com.inventario.ocupacion.dtos.OcupacionDTO;
import com.inventario.ocupacion.services.OcupacionService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ocupacion")
@RequiredArgsConstructor
public class OcupacionController {

	private final OcupacionService ocupacionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<OcupacionDTO>> findAll(@PageableDefault() Pageable pageable) {
		Page<OcupacionDTO> page = ocupacionService.findAll(pageable);

		if (page.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(page);
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<OcupacionDTO> findById(@PathVariable Long requestedId) {
		return ocupacionService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createOcupacion(@Valid @RequestBody OcupacionDTO ocupacionDTO,
			UriComponentsBuilder ucb) {
		return ResponseEntity
			.created(uriBuilderUtil.buildOcupacionUri((ocupacionService.create(ocupacionDTO)).getId(), ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateOcupacion(@PathVariable Long requestedId,
			@Valid @RequestBody OcupacionDTO ocupacionDTO) {
		ocupacionService.update(requestedId, ocupacionDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOcupacion(@PathVariable Long id) {
		ocupacionService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
