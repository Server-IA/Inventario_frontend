package com.coagronet.almacen.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.almacen.Almacen;
import com.coagronet.almacen.dtos.AlmacenDTO;
import com.coagronet.almacen.dtos.AlmacenMinimalDTO;

@Mapper(componentModel = "spring")
public interface AlmacenMapper {

    AlmacenMapper INSTANCE = Mappers.getMapper(AlmacenMapper.class);

    @Mapping(source = "sede.id", target = "sedeId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "espacio.id", target = "espacioId")
    AlmacenDTO toDTO(Almacen almacen);

    AlmacenMinimalDTO toMinimalDTO(Almacen almacen);

    @Mapping(source = "sedeId", target = "sede.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "espacioId", target = "espacio.id")
    Almacen toEntity(AlmacenDTO almacenDTO);
}
