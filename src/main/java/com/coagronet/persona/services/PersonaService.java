package com.coagronet.persona.services;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.persona.Persona;
import com.coagronet.persona.dtos.PersonaDTO;
import com.coagronet.persona.mappers.PersonaMapper;
import com.coagronet.persona.repositories.PersonaRepository;
import com.coagronet.utils.Constantes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;


    public Optional<PersonaDTO> findById(Long id){
        return personaRepository.findById(id)
                .map(personaMapper::toDto);
    }

    public Page<PersonaDTO> findAll(Pageable pageable){
        return personaRepository.findByEstadoIdNot(Constantes.ESTADO_INACTIVO, pageable)
                .map(personaMapper::toDto);
    }

    @Transactional
    public PersonaDTO create(PersonaDTO personaDTO){

        Persona savedPersona = personaMapper.toEntity(personaDTO);
        personaRepository.save(savedPersona);
        return personaMapper.toDto(savedPersona);
    }

    public void update(Long requestedId, PersonaDTO personaDTO){
        personaRepository.findById(requestedId)
                .orElseThrow(()->new NotFoundException("Persona no encontrada"));

        personaDTO.setId(requestedId);

        personaRepository.save(personaMapper.toEntity(personaDTO));
    }

    public void delete(Long id){
        Persona persona = personaRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Persona no encontrada"));
        personaRepository.deleteById(persona.getId());
    }
}
