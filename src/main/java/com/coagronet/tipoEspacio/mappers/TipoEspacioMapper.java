package com.coagronet.tipoEspacio.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.estado.Estado;
import com.coagronet.tipoEspacio.TipoEspacio;
import com.coagronet.tipoEspacio.dtos.TipoEspacioDTO;
import com.coagronet.tipoEspacio.dtos.TipoEspacioMinimalDTO;

@Mapper(componentModel = "spring")
public interface TipoEspacioMapper {

    TipoEspacioMapper INSTANCE = Mappers.getMapper(TipoEspacioMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    @Mapping(source = "empresa.id", target = "empresa")
    TipoEspacioDTO toDTO(TipoEspacio tipoEspacio);

    TipoEspacioMinimalDTO toMinimalDTO(TipoEspacio tipoEspacio);

    @Mapping(source = "estado", target = "estado.id")
    @Mapping(source = "empresa", target = "empresa.id")
    TipoEspacio toEntity(TipoEspacioDTO tipoEspacioDTO);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget TipoEspacio tipoEspacio,
            TipoEspacioDTO tipoEspacioDTO) {
        if (tipoEspacio.getEstado() == null && tipoEspacioDTO.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(tipoEspacioDTO.getEstado());
            tipoEspacio.setEstado(estado);
        }
    }

}
