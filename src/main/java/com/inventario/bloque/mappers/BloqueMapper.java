package com.inventario.bloque.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.bloque.Bloque;
import com.inventario.bloque.dtos.BloqueDTO;

@Mapper(componentModel = "spring")
public interface BloqueMapper {

	@Mapping(source = "sede.id", target = "sedeId")
	@Mapping(source = "tipoBloque.id", target = "tipoBloqueId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	BloqueDTO toDTO(Bloque bloque);

	@Mapping(source = "sedeId", target = "sede.id")
	@Mapping(source = "tipoBloqueId", target = "tipoBloque.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	Bloque toEntity(BloqueDTO bloqueDTO);

	@Mapping(source = "sede.id", target = "sedeId")
	@Mapping(source = "tipoBloque.id", target = "tipoBloqueId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(ignore = true, target = "empresaId")
	BloqueDTO toListDto(Bloque bloque);

}