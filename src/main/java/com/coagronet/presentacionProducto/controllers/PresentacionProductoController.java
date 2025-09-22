package com.coagronet.presentacionProducto.controllers;

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

import com.coagronet.presentacionProducto.dtos.PresentacionProductoDTO;
import com.coagronet.presentacionProducto.services.PresentacionProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/producto_presentacion")
@RequiredArgsConstructor
public class PresentacionProductoController {

	private final PresentacionProductoService presentacionProductoService;

	@GetMapping
	public ResponseEntity<Page<PresentacionProductoDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<PresentacionProductoDTO> presentacionProductoDTOS = presentacionProductoService.findAll(pageable);

		return ResponseEntity.ok(presentacionProductoDTOS);
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<PresentacionProductoDTO> findById(@PathVariable Long requestedId) {
		return presentacionProductoService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createPresentacionProducto(
			@Valid @RequestBody PresentacionProductoDTO newpresentacionProductoDTO, UriComponentsBuilder ucb) {
		PresentacionProductoDTO savedPresentacionProducto = presentacionProductoService
			.create(newpresentacionProductoDTO);
		URI locationOfNewPresentacionProducto = ucb.path("/{id}")
			.buildAndExpand(savedPresentacionProducto.getId())
			.toUri();
		return ResponseEntity.created(locationOfNewPresentacionProducto).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> putPresentacionProducto(@PathVariable Long requestedId,
			@Valid @RequestBody PresentacionProductoDTO presentacionProductoUpdate) {

		presentacionProductoService.update(requestedId, presentacionProductoUpdate);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePresentacionProducto(@PathVariable Long id) {

		presentacionProductoService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
