package com.coagronet.municipio.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.municipio.dtos.MunicipioDTO;
import com.coagronet.municipio.services.MunicipioService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/municipio")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MunicipioController {

	private final MunicipioService municipioService;
	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<MunicipioDTO>> findAll(@RequestParam Long departamentoId) {
		return ResponseEntity.ok(municipioService.findAll(departamentoId));
	}

	@GetMapping(params = "available=true")
	public ResponseEntity<List<MunicipioDTO>> findAllAvailable(@RequestParam Long departamentoId) {
		return ResponseEntity.ok(municipioService.findAllAvailable(departamentoId));
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<MunicipioDTO> findById(@PathVariable Long requestedId) {
		return municipioService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> createMunicipio(@Valid @RequestBody MunicipioDTO municipioDTO,
			UriComponentsBuilder ucb) {
		MunicipioDTO savedMunicipio = municipioService.create(municipioDTO);
		URI locationOfNewMunicipio = uriBuilderUtil.buildMunicipioUri(savedMunicipio.getId(), ucb);
		return ResponseEntity.created(locationOfNewMunicipio).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updateMunicipio(@PathVariable Long requestedId,
			@Valid @RequestBody MunicipioDTO municipioDTO) {
		municipioService.update(requestedId, municipioDTO);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMunicipio(@PathVariable Long id) {
		municipioService.delete(id);
		return ResponseEntity.noContent().build();
	}

}