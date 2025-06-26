package com.coagronet.almacen.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AlmacenController {

	private final AlmacenService almacenService;
	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<AlmacenDTO>> findAll() {
		return ResponseEntity.ok(almacenService.findAll());
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