package com.inventario.criterioEvaluacion.controllers;


import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.inventario.criterioEvaluacion.dtos.CriterioEvaluacionDTO;
import com.inventario.criterioEvaluacion.services.CriterioEvaluacionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/criterio_evaluacion")
@RequiredArgsConstructor
public class CriterioEvaluacionController {

	private final CriterioEvaluacionService criterioEvaluacionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<CriterioEvaluacionDTO>> findAll(@PageableDefault Pageable pageable) {

		Page<CriterioEvaluacionDTO> page = criterioEvaluacionService.findAll(pageable);

		if(page.isEmpty()){
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(page);
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<CriterioEvaluacionDTO> findById(@PathVariable Long requestedId) {
		return criterioEvaluacionService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createCriterioEvaluacion(
			@Valid @RequestBody CriterioEvaluacionDTO criterioEvaluacionDTO, UriComponentsBuilder ucb) {
		return ResponseEntity
			.created(uriBuilderUtil
				.buildCriterioEvaluacionUri((criterioEvaluacionService.create(criterioEvaluacionDTO)).getId(), ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateCriterioEvaluacion(@PathVariable Long requestedId,
			@Valid @RequestBody CriterioEvaluacionDTO criterioEvaluacionDTO) {
		criterioEvaluacionService.update(requestedId, criterioEvaluacionDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCriterioEvaluacion(@PathVariable Long id) {
		criterioEvaluacionService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
