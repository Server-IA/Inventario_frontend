package com.coagronet.subseccion.controllers;

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

import com.coagronet.subseccion.dtos.SubseccionDTO;
import com.coagronet.subseccion.services.SubseccionService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/subseccion")
@RequiredArgsConstructor
public class SubseccionController {

	private final SubseccionService subseccionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<SubseccionDTO>> findAll() {
		List<SubseccionDTO> subseccionDTOList = subseccionService.findAll();

		return subseccionDTOList.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(subseccionDTOList);
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<SubseccionDTO> findById(@PathVariable Long requestedId) {
		return subseccionService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<SubseccionDTO> createSubseccion(@RequestBody @Valid SubseccionDTO newSubseccion,
			UriComponentsBuilder ucb) {

		SubseccionDTO savedSubseccionDTO = subseccionService.create(newSubseccion);

		URI locationOfNewSubseccion = uriBuilderUtil.buildSubseccionUri(savedSubseccionDTO.getId(), ucb);

		return ResponseEntity.created(locationOfNewSubseccion).build();

	}

	@PutMapping("/{requestedId}")
	private ResponseEntity<Void> putSubseccion(@PathVariable Long requestedId,
			@RequestBody @Valid SubseccionDTO subseccionDTOUpdate) {
		subseccionService.update(requestedId, subseccionDTOUpdate);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	private ResponseEntity<Void> deleteSubseccion(@PathVariable Long id) {

		subseccionService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
