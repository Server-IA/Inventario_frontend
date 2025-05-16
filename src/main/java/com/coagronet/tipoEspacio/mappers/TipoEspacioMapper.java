package com.coagronet.tipoEspacio.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.coagronet.tipoEspacio.TipoEspacio;
import com.coagronet.tipoEspacio.dtos.TipoEspacioDTO;

@Mapper(componentModel = "spring")
public interface TipoEspacioMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	TipoEspacioDTO toDTO(TipoEspacio tipoEspacio);

	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	TipoEspacio toEntity(TipoEspacioDTO tipoEspacioDTO);

	@Mapping(target = "estadoId", source = "estado.id")
	@Mapping(target = "empresaId", ignore = true)
	TipoEspacioDTO toListDto(TipoEspacio tipoEspacio);

}
