package com.inventario.tipoSede.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.tipoSede.TipoSede;
import com.inventario.tipoSede.dtos.TipoSedeDTO;

@Mapper(componentModel = "spring")
public interface TipoSedeMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	TipoSedeDTO toDTO(TipoSede tipoSede);

	@Mapping(target = "empresaId", ignore = true)
	@Mapping(source = "estado.id", target = "estadoId")
	TipoSedeDTO toListDTO(TipoSede tipoSede);

	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	TipoSede toEntity(TipoSedeDTO tipoSedeDTO);

}
