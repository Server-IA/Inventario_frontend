package com.coagronet.modulo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.menu.dtos.MenuModuloResponseDTO;
import com.coagronet.menu.repositories.projections.SubModuloRow;

@Mapper(componentModel = "spring")
public interface ModuloMapper {

	@Mapping(target = "id", source = "modNombreId")
	@Mapping(target = "nombre", source = "modNombre")
	@Mapping(target = "url", source = "modUrl")
	@Mapping(target = "icono", source = "modIcon")
	MenuModuloResponseDTO toDTO(SubModuloRow row);

}
