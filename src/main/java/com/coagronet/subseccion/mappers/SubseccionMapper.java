package com.coagronet.subseccion.mappers;

import com.coagronet.subseccion.Subseccion;
import com.coagronet.subseccion.dtos.SubseccionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubseccionMapper {

    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(source = "seccion.id", target = "seccionId")
    SubseccionDTO toDTO(Subseccion subseccion);


    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    @Mapping(source = "seccionId", target = "seccion.id")
    Subseccion toEntity(SubseccionDTO subseccionDTO);
}
