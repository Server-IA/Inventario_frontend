package com.coagronet.pais.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.pais.Pais;
import com.coagronet.pais.dtos.PaisDTO;

@Mapper(componentModel = "spring")
public interface PaisMapper {

    @Mapping(source = "nombre", target = "name")
    PaisDTO toDTO(Pais pais);
}
