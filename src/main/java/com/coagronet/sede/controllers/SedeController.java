package com.coagronet.sede.controllers;

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

import com.coagronet.sede.dtos.SedeDTO;
import com.coagronet.sede.services.SedeService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sede")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SedeController {

	private final SedeService sedeService;
	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<SedeDTO>> findAll() {
		return ResponseEntity.ok(sedeService.findAll());
	}

	@GetMapping(params = "available=true")
	public ResponseEntity<List<SedeDTO>> findAllAvailable() {
		return ResponseEntity.ok(sedeService.findAllAvailable());
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<SedeDTO> findById(@PathVariable Long requestedId) {
		return sedeService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createSede(@Valid @RequestBody SedeDTO sedeDTO, UriComponentsBuilder ucb) {
		SedeDTO savedSede = sedeService.create(sedeDTO);
		URI locationOfNewSede = uriBuilderUtil.buildSedeUri(savedSede.getId(), ucb);
		return ResponseEntity.created(locationOfNewSede).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateSede(@PathVariable Long requestedId, @Valid @RequestBody SedeDTO sedeDTO) {
		sedeService.update(requestedId, sedeDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSede(@PathVariable Long id) {
		sedeService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
