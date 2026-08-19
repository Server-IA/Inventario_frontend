package com.inventario.presentacion.controllers;

import java.net.URI;
import java.util.List;

import com.inventario.presentacion.dtos.PresentacionDTO;
import com.inventario.presentacion.services.PresentacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1/presentacion")
@RequiredArgsConstructor
public class PresentacionController {

	private final PresentacionService presentacionService;

	@GetMapping("/{requestedId}")
	private ResponseEntity<PresentacionDTO> findById(@PathVariable Long requestedId) {
		return presentacionService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<List<PresentacionDTO>> findAll() {
		List<PresentacionDTO> presentacionDTOList = presentacionService.findAll();
		return presentacionDTOList.isEmpty() ? ResponseEntity.noContent().build()
				: ResponseEntity.ok(presentacionDTOList);
	}

	@PostMapping
	public ResponseEntity<PresentacionDTO> createPresentacion(
			@Valid @RequestBody PresentacionDTO newPresentacionRequest, UriComponentsBuilder ucb) {

		PresentacionDTO savedPresentacion = presentacionService.create(newPresentacionRequest);

		URI locationOfNewPresentacion = ucb.path("/{id}").buildAndExpand(savedPresentacion.getId()).toUri();
		return ResponseEntity.created(locationOfNewPresentacion).body(savedPresentacion);
	}

	@PutMapping("/{requestedId}")
	private ResponseEntity<Void> putPresentacion(@PathVariable Long requestedId,
			@Valid @RequestBody PresentacionDTO presentacionUpdate) {

		presentacionService.update(requestedId, presentacionUpdate);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	private ResponseEntity<Void> deletePresentacion(@PathVariable Long id) {
		presentacionService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
