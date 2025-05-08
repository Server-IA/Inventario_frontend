package com.coagronet.espacio.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.espacio.Espacio;
import com.coagronet.espacio.dtos.EspacioDTO;
import com.coagronet.estado.Estado;

@Mapper(componentModel = "spring")
public interface EspacioMapper {

    EspacioMapper INSTANCE = Mappers.getMapper(EspacioMapper.class);

    @Mapping(source = "bloque.id", target = "bloque")
    @Mapping(source = "tipoEspacio.id", target = "tipoEspacio")
    @Mapping(source = "estado.id", target = "estado")
    EspacioDTO toDTO(Espacio espacio);

    @Mapping(source = "bloque", target = "bloque.id")
    @Mapping(source = "tipoEspacio", target = "tipoEspacio.id")
    @Mapping(source = "estado", target = "estado.id")
    Espacio toEntity(EspacioDTO espacioDTO);


    @Mapping(source = "id", target = "id")
    @Mapping(target = "bloque", ignore = true)
    @Mapping(target = "tipoEspacio", ignore = true)
    @Mapping(target = "geolocalizacion", ignore = true)
    @Mapping(target = "coordenadas", ignore = true)
    @Mapping(target = "descripcion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    EspacioDTO toMinimalDTO(Espacio espacio);


    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget Espacio espacio, EspacioDTO espacioDTO) {
        if (espacio.getEstado() == null && espacioDTO.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(espacioDTO.getEstado());
            espacio.setEstado(estado);
        }
    }

}
