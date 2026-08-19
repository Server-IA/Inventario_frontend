package com.inventario.produccion.controllers;

import java.net.URI;

import com.inventario.produccion.dtos.ProduccionCreateDTO;
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

import com.inventario.produccion.dtos.ProduccionDTO;
import com.inventario.produccion.services.ProduccionService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/produccion")
@RequiredArgsConstructor
public class ProduccionController {

	private final ProduccionService produccionService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<ProduccionDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<ProduccionDTO> produccionDTOList = produccionService.findAll(pageable);

		return ResponseEntity.ok(produccionDTOList);

	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<ProduccionDTO> findById(@PathVariable Long requestedId) {
		return produccionService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());

	}

	@PostMapping
	public ResponseEntity<Void> crearProduccion(@RequestBody @Valid ProduccionCreateDTO produccionDTO,
												UriComponentsBuilder ucb) {
		ProduccionDTO savedProduccionDTO = produccionService.create(produccionDTO);

		URI locationOfNewProduccion = uriBuilderUtil.buildProduccion(savedProduccionDTO.getId(), ucb);
		return ResponseEntity.created(locationOfNewProduccion).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> actualizarProduccion(@PathVariable Long requestedId,
			@RequestBody ProduccionDTO produccionDTO) {

		produccionService.update(requestedId, produccionDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{requestedId}")
	public ResponseEntity<Void> eliminarProduccion(@PathVariable Long requestedId) {
		produccionService.delete(requestedId);
		return ResponseEntity.noContent().build();
	}

}
