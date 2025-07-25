package com.coagronet.persona.controllers;

import java.net.URI;

import com.coagronet.persona.services.PersonaService;
import lombok.RequiredArgsConstructor;
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

import com.coagronet.persona.dtos.PersonaDTO;

@RestController
@RequestMapping("/api/v1/persona")
@RequiredArgsConstructor
public class PersonaController {

	private final PersonaService personaService;

	@GetMapping("/{requestedId}")
	private ResponseEntity<PersonaDTO> findById(@PathVariable Long requestedId) {
		return personaService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	private ResponseEntity<Void> createPersona(@RequestBody PersonaDTO newPersonaRequest, UriComponentsBuilder ucb) {
		PersonaDTO savedPersona = personaService.create(newPersonaRequest);
		URI locationOfNewPersona = ucb.path("/api/v1/personas/{id}").buildAndExpand(savedPersona.getId()).toUri();
		return ResponseEntity.created(locationOfNewPersona).build();
	}

	@GetMapping
	private ResponseEntity<Page<PersonaDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<PersonaDTO> personas = personaService.findAll(pageable);
		return ResponseEntity.ok(personas);
	}

	@PutMapping("/{requestedId}")
	private ResponseEntity<Void> putPersona(@PathVariable Long requestedId, @RequestBody PersonaDTO personaDTOUpdate) {
		personaService.update(requestedId, personaDTOUpdate);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	private ResponseEntity<Void> deletePersona(@PathVariable Long id) {
		personaService.delete(id);
		return ResponseEntity.ok().build();
	}

}
