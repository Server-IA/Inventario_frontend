package com.coagronet.tipoEspacio.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.tipoEspacio.TipoEspacio;
import com.coagronet.tipoEspacio.dtos.TipoEspacioDTO;

@Mapper(componentModel = "spring")
public interface TipoEspacioMapper {

    TipoEspacioMapper INSTANCE = Mappers.getMapper(TipoEspacioMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    TipoEspacioDTO toDTO(TipoEspacio tipoEspacio);

    @Mapping(source = "estado", target = "estado.id")
    TipoEspacio toEntity(TipoEspacioDTO tipoEspacioDTO);

}
