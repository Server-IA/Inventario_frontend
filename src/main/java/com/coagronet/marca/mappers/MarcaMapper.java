package com.coagronet.marca.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.marca.Marca;
import com.coagronet.marca.dtos.MarcaDTO;

@Mapper(componentModel = "spring")
public interface MarcaMapper {

    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    MarcaDTO toDTO(Marca marca);

    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    Marca toEntity(MarcaDTO marcaDTO);

    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    MarcaDTO toListDto(Marca marca);

}
