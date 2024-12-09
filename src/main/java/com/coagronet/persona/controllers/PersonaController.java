package com.coagronet.persona.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.coagronet.persona.Persona;
import com.coagronet.persona.dtos.PersonaDTO;
import com.coagronet.persona.mappers.PersonaMapper;
import com.coagronet.persona.repositories.PersonaRepository;

@RestController
@RequestMapping("/api/v1/persona")
@CrossOrigin(origins = "*")
public class PersonaController {

    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;

    public PersonaController(
            PersonaRepository personaRepository,
            PersonaMapper personaMapper) {
        this.personaRepository = personaRepository;
        this.personaMapper = personaMapper;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<PersonaDTO> findById(@PathVariable Long requestedId) {
        return personaRepository
                .findById(requestedId)
                .map(personaMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createPersona(@RequestBody PersonaDTO newPersonaRequest, UriComponentsBuilder ucb) {
        PersonaDTO newPersona = new PersonaDTO(
                null,
                newPersonaRequest.getTipoIdentificacion(),
                newPersonaRequest.getIdentificacion(),
                newPersonaRequest.getNombre(),
                newPersonaRequest.getApellido(),
                newPersonaRequest.getGenero(),
                newPersonaRequest.getFechaNacimiento(),
                newPersonaRequest.getEstrato(),
                newPersonaRequest.getDireccion(),
                newPersonaRequest.getEmail(),
                newPersonaRequest.getCelular(),
                newPersonaRequest.getEstado());
        Persona savedPersona = personaMapper.toEntity(newPersona);
        personaRepository.save(savedPersona);
        URI locationOfNewPersona = ucb
                .path("/api/v1/personas/{id}")
                .buildAndExpand(savedPersona.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewPersona).build();
    }

    @GetMapping
    private ResponseEntity<Page<PersonaDTO>> findAll(@PageableDefault Pageable pageable) {
        Page<PersonaDTO> page = personaRepository.findByEstadoIdNot(2, pageable)
                .map(personaMapper::toDto);
        return page.hasContent()
                ? ResponseEntity.ok(page)
                : ResponseEntity.noContent().build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<PersonaDTO> putPersona(@PathVariable Long requestedId,
            @RequestBody PersonaDTO personaDTOUpdate) {
        Persona persona = personaRepository.findById(requestedId).orElse(null);
        if (null != persona) {
            PersonaDTO updatedPersonaDTO = new PersonaDTO(
                    requestedId,
                    personaDTOUpdate.getTipoIdentificacion(),
                    personaDTOUpdate.getIdentificacion(),
                    personaDTOUpdate.getNombre(),
                    personaDTOUpdate.getApellido(),
                    personaDTOUpdate.getGenero(),
                    personaDTOUpdate.getFechaNacimiento(),
                    personaDTOUpdate.getEstrato(),
                    personaDTOUpdate.getDireccion(),
                    personaDTOUpdate.getEmail(),
                    personaDTOUpdate.getCelular(),
                    personaDTOUpdate.getEstado());
            Persona updatedPersona = personaMapper.toEntity(updatedPersonaDTO);
            personaRepository.save(updatedPersona);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deletePersona(@PathVariable Long id) {
        try {
            if (personaRepository.existsById(id)) {
                personaRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
