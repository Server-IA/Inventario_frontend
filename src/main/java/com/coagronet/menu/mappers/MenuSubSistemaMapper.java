package com.coagronet.menu.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.menu.dtos.MenuSubSistemaResponseDTO;
import com.coagronet.subsistema.SubSistema;

@Mapper(componentModel = "spring")
public interface MenuSubSistemaMapper {

    @Mapping(target = "icono", source = "icon")
    MenuSubSistemaResponseDTO toDTO(SubSistema subSistema);

}
