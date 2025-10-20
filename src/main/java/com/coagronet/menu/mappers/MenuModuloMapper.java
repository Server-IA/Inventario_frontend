package com.coagronet.menu.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.menu.dtos.MenuModuloResponseDTO;
import com.coagronet.modulo.Modulo;

@Mapper(componentModel = "spring")
public interface MenuModuloMapper {

    @Mapping(target = "id", source = "nombreId")
    @Mapping(target = "icono", source = "icon")
    MenuModuloResponseDTO toDTO(Modulo modulo);

}
