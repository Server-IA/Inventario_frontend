package com.coagronet.persona.controllers;

import java.net.URI;

import com.coagronet.persona.services.PersonaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.coagronet.persona.dtos.PersonaPreloadResponse;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Personas", description = "API para la gestión y consulta de datos de personas")
public class PersonaController {

	private final PersonaService personaService;

	@GetMapping("/{requestedId}")
	private ResponseEntity<PersonaDTO> findById(@PathVariable Long requestedId) {
		return personaService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/api/v1/persona")
	private ResponseEntity<Void> createPersona(@RequestBody PersonaDTO newPersonaRequest, UriComponentsBuilder ucb) {
		PersonaDTO savedPersona = personaService.create(newPersonaRequest);
		URI locationOfNewPersona = ucb.path("/api/v1/personas/{id}").buildAndExpand(savedPersona.getId()).toUri();
		return ResponseEntity.created(locationOfNewPersona).build();
	}

	@GetMapping("/api/v1/persona")
	private ResponseEntity<Page<PersonaDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<PersonaDTO> personas = personaService.findAll(pageable);
		return ResponseEntity.ok(personas);
	}

	@PutMapping("/api/v1/persona/{requestedId}")
	private ResponseEntity<Void> putPersona(@PathVariable Long requestedId, @RequestBody PersonaDTO personaDTOUpdate) {
		personaService.update(requestedId, personaDTOUpdate);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/api/v1/persona/{id}")
	private ResponseEntity<Void> deletePersona(@PathVariable Long id) {
		personaService.delete(id);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Consultar persona por identificación", description = "Retorna los datos personales básicos y verifica si la persona ya tiene un usuario de sistema creado. Utilizado para precarga de formularios.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Persona encontrada exitosamente."),
			@ApiResponse(responseCode = "404", description = "Persona no encontrada. El cliente puede proceder a crearla desde cero."),
			@ApiResponse(responseCode = "403", description = "Acceso denegado. Se requieren permisos de gestión de usuarios.")
	})
	@GetMapping("/api/v1/personas/{identificacion}")
	@PreAuthorize("hasAuthority('PERSONA_READ') or hasAnyRole('ADMINISTRADOR_SISTEMA', 'ADMINISTRADOR_EMPRESA')")
	public ResponseEntity<PersonaPreloadResponse> obtenerPersonaPorIdentificacion(
			@PathVariable String identificacion) {

		PersonaPreloadResponse response = personaService.buscarPorIdentificacion(identificacion);
		return ResponseEntity.ok(response);
	}

}
