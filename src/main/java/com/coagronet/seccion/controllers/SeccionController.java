package com.coagronet.seccion.controllers;

import java.util.List;

import com.coagronet.kardex.dtos.KardexDTO;
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

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.seccion.dtos.SeccionDTO;
import com.coagronet.seccion.services.SeccionService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/seccion")
@RequiredArgsConstructor
public class SeccionController {

	private final SeccionService seccionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<SeccionDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<SeccionDTO> page = seccionService.findAll(pageable);

		if(page.isEmpty()){
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(page); // 200 OK
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<SeccionDTO> findById(@PathVariable Long requestedId) {
		return ResponseEntity.ok(seccionService.findById(requestedId)
			.orElseThrow(() -> new NotFoundException("La sección no fue encontrada")));
	}

	@PostMapping
	public ResponseEntity<Void> createSeccion(@Valid @RequestBody SeccionDTO seccionDTO, UriComponentsBuilder ucb) {
		Long newId = seccionService.create(seccionDTO).getId();
		return ResponseEntity.created(uriBuilderUtil.buildSeccionUri(newId, ucb)).build(); // 201
																							// Created
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateSeccion(@PathVariable Long requestedId,
			@Valid @RequestBody SeccionDTO seccionDTO) {
		seccionService.update(requestedId, seccionDTO);
		return ResponseEntity.noContent().build(); // 204 No Content
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSeccion(@PathVariable Long id) {
		seccionService.delete(id);
		return ResponseEntity.noContent().build(); // 204 No Content
	}

}