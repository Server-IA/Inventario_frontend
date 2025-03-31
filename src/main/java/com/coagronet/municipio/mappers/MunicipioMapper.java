package com.coagronet.municipio.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.municipio.Municipio;
import com.coagronet.municipio.dtos.MunicipioDTO;

@Mapper(componentModel = "spring")
public interface MunicipioMapper {

    @Mapping(source = "nombre", target = "name")
    MunicipioDTO toDTO(Municipio municipio);
}