package com.coagronet.persona.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.persona.Persona;
import com.coagronet.persona.dtos.PersonaDTO;

@Mapper(componentModel = "spring")
public interface PersonaMapper {

	PersonaMapper INSTANCE = Mappers.getMapper(PersonaMapper.class);

	@Mapping(source = "tipoIdentificacion.id", target = "tipoIdentificacion")
	@Mapping(source = "estado.id", target = "estado")
	PersonaDTO toDto(Persona persona);

	@Mapping(source = "tipoIdentificacion", target = "tipoIdentificacion.id")
	@Mapping(source = "estado", target = "estado.id")
	Persona toEntity(PersonaDTO personaDTO);

}
