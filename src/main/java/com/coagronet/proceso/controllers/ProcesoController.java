package com.coagronet.proceso.controllers;

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

import com.coagronet.proceso.dtos.ProcesoDTO;
import com.coagronet.proceso.services.ProcesoService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/proceso")
@RequiredArgsConstructor
public class ProcesoController {

	private final ProcesoService procesoService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<ProcesoDTO>> findAll() {
		return ResponseEntity.ok(procesoService.findAll());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<ProcesoDTO> findById(@PathVariable Long requestedId) {
		return procesoService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createProceso(@Valid @RequestBody ProcesoDTO procesoDTO, UriComponentsBuilder ucb) {
		return ResponseEntity.created(uriBuilderUtil.buildProcesoUri((procesoService.create(procesoDTO)).getId(), ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateProceso(@PathVariable Long requestedId,
			@Valid @RequestBody ProcesoDTO procesoDTO) {
		procesoService.update(requestedId, procesoDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProceso(@PathVariable Long id) {
		procesoService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
