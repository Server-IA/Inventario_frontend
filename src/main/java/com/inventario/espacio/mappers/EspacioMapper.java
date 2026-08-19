package com.inventario.espacio.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.espacio.Espacio;
import com.inventario.espacio.dtos.EspacioDTO;

@Mapper(componentModel = "spring")
public interface EspacioMapper {

	@Mapping(source = "bloque.id", target = "bloqueId")
	@Mapping(source = "tipoEspacio.id", target = "tipoEspacioId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	EspacioDTO toDTO(Espacio espacio);

	@Mapping(source = "bloqueId", target = "bloque.id")
	@Mapping(source = "tipoEspacioId", target = "tipoEspacio.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	Espacio toEntity(EspacioDTO espacioDTO);

	@Mapping(source = "bloque.id", target = "bloqueId")
	@Mapping(source = "tipoEspacio.id", target = "tipoEspacioId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(ignore = true, target = "empresaId")
	EspacioDTO toListDto(Espacio espacio);

}