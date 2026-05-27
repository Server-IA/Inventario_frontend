package com.coagronet.departamento.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.departamento.Departamento;
import com.coagronet.departamento.dtos.DepartamentoDTO;

@Mapper(componentModel = "spring")
public interface DepartamentoMapper {

	@Mapping(source = "pais.id", target = "paisId")
	@Mapping(source = "estado.id", target = "estadoId")
	DepartamentoDTO toDTO(Departamento departamento);

	@Mapping(source = "paisId", target = "pais.id")
	@Mapping(source = "estadoId", target = "estado.id")
	Departamento toEntity(DepartamentoDTO departamentoDTO);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "nombre", target = "nombre")
	@Mapping(target = "paisId", source = "pais.id")
	@Mapping(source = "codigo", target = "codigo")
	@Mapping(source = "acronimo", target = "acronimo")
	@Mapping(target = "estadoId", source = "estado.id")
	DepartamentoDTO toListDto(Departamento departamento);

}
