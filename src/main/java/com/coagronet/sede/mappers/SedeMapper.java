package com.coagronet.sede.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.sede.Sede;
import com.coagronet.sede.dtos.SedeDTO;

@Mapper(componentModel = "spring")
public interface SedeMapper {

	@Mapping(source = "grupo.id", target = "grupoId")
	@Mapping(source = "tipoSede.id", target = "tipoSedeId")
	@Mapping(source = "empresa.id", target = "empresaId")
	@Mapping(source = "municipio.id", target = "municipioId")
	@Mapping(source = "estado.id", target = "estadoId")
	SedeDTO toDTO(Sede sede);

	@Mapping(source = "grupoId", target = "grupo.id")
	@Mapping(source = "tipoSedeId", target = "tipoSede.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	@Mapping(source = "municipioId", target = "municipio.id")
	@Mapping(source = "estadoId", target = "estado.id")
	Sede toEntity(SedeDTO sedeDTO);

	@Mapping(source = "grupo.id", target = "grupoId")
	@Mapping(source = "tipoSede.id", target = "tipoSedeId")
	@Mapping(ignore = true, target = "empresaId")
	@Mapping(source = "municipio.id", target = "municipioId")
	@Mapping(source = "estado.id", target = "estadoId")
	SedeDTO toListDto(Sede sede);

}
