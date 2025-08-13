package com.coagronet.almacen.controllers;

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

import com.coagronet.almacen.dtos.AlmacenDTO;
import com.coagronet.almacen.services.AlmacenService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/almacen")
@RequiredArgsConstructor
public class AlmacenController {

	private final AlmacenService almacenService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<AlmacenDTO>> findAll(@PageableDefault() Pageable pageable) {

		Page<AlmacenDTO> page = almacenService.findAll(pageable);
		if (page.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(page);
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<AlmacenDTO> findById(@PathVariable Long requestedId) {
		return almacenService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createAlmacen(@Valid @RequestBody AlmacenDTO almacenDTO, UriComponentsBuilder ucb) {
		AlmacenDTO savedAlmacen = almacenService.create(almacenDTO);
		URI locationOfNewAlmacen = uriBuilderUtil.buildAlmacenUri(savedAlmacen.getId(), ucb);
		return ResponseEntity.created(locationOfNewAlmacen).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateAlmacen(@PathVariable Long requestedId,
			@Valid @RequestBody AlmacenDTO almacenDTO) {
		almacenService.update(requestedId, almacenDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAlmacen(@PathVariable Long id) {
		almacenService.delete(id);
		return ResponseEntity.noContent().build();
	}

}