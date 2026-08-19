package com.inventario.estado.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.inventario.estado.Estado;
import com.inventario.estado.dtos.EstadoDTO;

@Mapper(componentModel = "spring")
public interface EstadoMapper {

	@Mapping(source = "estadoCategoria.id", target = "estadoCategoriaId")
	EstadoDTO toDTO(Estado estado);

	@Mapping(source = "estadoCategoriaId", target = "estadoCategoria.id")
	Estado toEntity(EstadoDTO estadoDTO);

	@Mapping(target = "descripcion", ignore = true)
	@Mapping(target = "acronimo", ignore = true)
	@Mapping(target = "id", source = "id")
	@Mapping(target = "nombre", source = "nombre")
	@Mapping(source = "estadoCategoria.id", target = "estadoCategoriaId")
	EstadoDTO toShortDTO(Estado estado);

}