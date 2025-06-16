package com.coagronet.almacen.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.almacen.Almacen;
import com.coagronet.almacen.dtos.AlmacenDTO;

@Mapper(componentModel = "spring")
public interface AlmacenMapper {

	@Mapping(source = "espacio.id", target = "espacioId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	AlmacenDTO toDTO(Almacen almacen);

	@Mapping(source = "espacioId", target = "espacio.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	Almacen toEntity(AlmacenDTO almacenDTO);

	@Mapping(source = "espacio.id", target = "espacioId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(ignore = true, target = "empresaId")
	AlmacenDTO toListDto(Almacen almacen);
}