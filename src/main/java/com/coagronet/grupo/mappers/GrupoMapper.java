package com.coagronet.grupo.mappers;

import com.coagronet.grupo.Grupo;
import com.coagronet.grupo.dtos.GrupoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GrupoMapper {

	@Mapping(source = "empresa.id", target = "empresaId")
	@Mapping(source = "estado.id", target = "estadoId")
	GrupoDTO toDTO(Grupo grupo);

	@Mapping(source = "empresaId", target = "empresa.id")
	@Mapping(source = "estadoId", target = "estado.id")
	Grupo toEntity(GrupoDTO grupoDTO);

	@Mapping(target = "empresaId", ignore = true)
	@Mapping(target = "estadoId", source = "estado.id")
	GrupoDTO toListDto(Grupo grupo);

}