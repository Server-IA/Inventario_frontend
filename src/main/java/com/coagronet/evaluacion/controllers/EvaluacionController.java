package com.coagronet.evaluacion.controllers;

import java.util.List;

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

import com.coagronet.evaluacion.dtos.EvaluacionDTO;
import com.coagronet.evaluacion.services.EvaluacionService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/evaluacion")
@RequiredArgsConstructor
public class EvaluacionController {

	private final EvaluacionService evaluacionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<EvaluacionDTO>> findAll(@PageableDefault Pageable pageable) {
		return ResponseEntity.ok(evaluacionService.findAll(pageable));
	}

	@GetMapping("/tipo/{tipoEvaluacion}")
	public ResponseEntity<List<EvaluacionDTO>> findByTipoEvaluacion(@PathVariable Long tipoEvaluacion) {
		List<EvaluacionDTO> list = evaluacionService.findAllByTipoEvaluacionId(tipoEvaluacion);

		if (list.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(list);
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<EvaluacionDTO> findById(@PathVariable Long requestedId) {
		return evaluacionService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createEvaluacion(@Valid @RequestBody EvaluacionDTO evaluacionDTO,
			UriComponentsBuilder ucb) {
		return ResponseEntity
			.created(uriBuilderUtil.buildEvaluacionUri((evaluacionService.create(evaluacionDTO)).getId(), ucb))
			.build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateEvaluacion(@PathVariable Long requestedId,
			@Valid @RequestBody EvaluacionDTO evaluacionDTO) {
		evaluacionService.update(requestedId, evaluacionDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEvaluacion(@PathVariable Long id) {
		evaluacionService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
