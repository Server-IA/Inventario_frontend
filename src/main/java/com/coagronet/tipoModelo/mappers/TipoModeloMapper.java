package com.coagronet.tipoModelo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.tipoModelo.TipoModelo;
import com.coagronet.tipoModelo.dtos.TipoModeloDTO;

@Mapper(componentModel = "spring")
public interface TipoModeloMapper {
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    TipoModeloDTO toDto(TipoModelo tipoModelo);

    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    TipoModelo toEntity(TipoModeloDTO tipoModeloDTO);

    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    TipoModeloDTO toListDTO(TipoModelo tipoModelo);
}

