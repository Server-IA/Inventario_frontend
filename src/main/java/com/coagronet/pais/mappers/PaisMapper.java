package com.coagronet.pais.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.pais.Pais;
import com.coagronet.pais.dtos.PaisDTO;

@Mapper(componentModel = "spring")
public interface PaisMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	PaisDTO toDTO(Pais pais);

	@Mapping(source = "estadoId", target = "estado.id")
	Pais toEntity(PaisDTO paisDTO);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "nombre", target = "nombre")
	@Mapping(source = "codigo", target = "codigo")
	@Mapping(source = "acronimo", target = "acronimo")
	@Mapping(target = "estadoId", source = "estado.id")
	PaisDTO toListDto(Pais pais);

}
