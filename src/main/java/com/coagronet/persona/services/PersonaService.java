package com.coagronet.persona.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.RecursoNoEncontradoException;
import com.coagronet.persona.Persona;
import com.coagronet.persona.dtos.PersonaDTO;
import com.coagronet.persona.dtos.PersonaPreloadResponse;
import com.coagronet.persona.mappers.PersonaMapper;
import com.coagronet.persona.repositories.PersonaRepository;
import com.coagronet.user.User;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.utils.Constantes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonaService {

	private final PersonaRepository personaRepository;

	private final UserRepository userRepository;

	private final PersonaMapper personaMapper;

	public Optional<PersonaDTO> findById(Long id) {
		return personaRepository.findById(id).map(personaMapper::toDto);
	}

	public Page<PersonaDTO> findAll(Pageable pageable) {
		return personaRepository.findByEstadoIdNot(Constantes.ESTADO_INACTIVO, pageable).map(personaMapper::toDto);
	}

	@Transactional
	public PersonaDTO create(PersonaDTO personaDTO) {

		Persona savedPersona = personaMapper.toEntity(personaDTO);
		personaRepository.save(savedPersona);
		return personaMapper.toDto(savedPersona);
	}

	public void update(Long requestedId, PersonaDTO personaDTO) {
		personaRepository.findById(requestedId).orElseThrow(() -> new NotFoundException("Persona no encontrada"));

		personaDTO.setId(requestedId);

		personaRepository.save(personaMapper.toEntity(personaDTO));
	}

	public void delete(Long id) {
		Persona persona = personaRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Persona no encontrada"));
		personaRepository.deleteById(persona.getId());
	}

	@Transactional(readOnly = true)
	public PersonaPreloadResponse buscarPorIdentificacion(String identificacion) {

		// 1. Buscar la persona. Si no existe, lanza excepción con mensaje claro.
		Persona persona = personaRepository.findByIdentificacion(identificacion)
				.orElseThrow(() -> new RecursoNoEncontradoException("Persona", identificacion));

		// 2. Buscar si la persona ya tiene un usuario de sistema asociado
		Optional<User> usuarioOpt = userRepository.findByPersonaId(persona.getId());

		// 3. Mapear la entidad al DTO
		return new PersonaPreloadResponse(
				persona.getTipoIdentificacion().getId(),
				persona.getIdentificacion(),
				persona.getNombre(),
				persona.getApellido(),
				persona.getEmailPersonal(),
				persona.getGenero(),
				persona.getFechaNacimiento(),
				persona.getDireccion(),
				persona.getCelular(),
				persona.getEstrato(),
				usuarioOpt.isPresent(),
				usuarioOpt.map(User::getUsername).orElse(null));
	}

}
