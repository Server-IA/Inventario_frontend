package com.coagronet.almacen.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.almacen.Almacen;
import com.coagronet.almacen.dtos.DTOAlmacen;

@Mapper(componentModel = "spring")
public interface AlmacenMapper {

    AlmacenMapper INSTANCE = Mappers.getMapper(AlmacenMapper.class);

    @Mapping(source = "sede.id", target = "sede")
    @Mapping(source = "estado.id", target = "estado")
    DTOAlmacen toDTO(Almacen almacen);

    @Mapping(source = "sede", target = "sede.id")
    @Mapping(source = "estado", target = "estado.id")
    Almacen toEntity(DTOAlmacen dtoAlmacen);

    @Mapping(source = "sede", target = "sede.id")
    @Mapping(source = "estado", target = "estado.id")
    void updateEntityFromDTO(DTOAlmacen dtoAlmacen, @MappingTarget Almacen almacen);
}
