package com.coagronet.marca.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.marca.Marca;
import com.coagronet.marca.dtos.MarcaDTO;

@Mapper(componentModel = "spring")
public interface MarcaMapper {
    MarcaMapper INSTANCE = Mappers.getMapper(MarcaMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    MarcaDTO toDTO(Marca marca);

    @Mapping(source = "estado", target = "estado.id")
    Marca toEntity(MarcaDTO marcaDTO);
}
