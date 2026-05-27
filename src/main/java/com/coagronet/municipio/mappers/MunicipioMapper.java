package com.coagronet.municipio.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.coagronet.municipio.Municipio;
import com.coagronet.municipio.dtos.MunicipioDTO;

@Mapper(componentModel = "spring")
public interface MunicipioMapper {

	@Mapping(source = "departamento.id", target = "departamentoId")
	@Mapping(source = "estado.id", target = "estadoId")
	MunicipioDTO toDTO(Municipio municipio);

	@Mapping(source = "departamentoId", target = "departamento.id")
	@Mapping(source = "estadoId", target = "estado.id")
	Municipio toEntity(MunicipioDTO municipioDTO);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "nombre", source = "nombre")
	@Mapping(target = "departamentoId", source = "departamento.id")
	@Mapping(target = "codigo", source = "codigo")
	@Mapping(target = "acronimo", source = "acronimo")
	@Mapping(target = "estadoId", source = "estado.id")
	MunicipioDTO toListDto(Municipio municipio);

}
