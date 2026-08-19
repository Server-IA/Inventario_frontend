package com.inventario.subSeccionDispositivo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.subSeccionDispositivo.SubseccionDispositivo;
import com.inventario.subSeccionDispositivo.dtos.SubseccionDispositivoDTO;

@Mapper(componentModel = "spring")
public interface SubseccionDispositivoMapper {
    @Mapping(source = "subseccion.id", target = "subseccionId")
    @Mapping(source = "tipoDispositivo.id", target = "tipoDispositivoId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    SubseccionDispositivoDTO toDto(SubseccionDispositivo subseccionDispositivo);
    
    @Mapping(source = "subseccionId", target = "subseccion.id")
    @Mapping(source = "tipoDispositivoId", target = "tipoDispositivo.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    SubseccionDispositivo toEntity(SubseccionDispositivoDTO subSeccionDispositivoDTO);

    @Mapping(source = "subseccion.id", target = "subseccionId")
    @Mapping(source = "tipoDispositivo.id", target = "tipoDispositivoId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    SubseccionDispositivoDTO toListDTO(SubseccionDispositivo subseccionDispositivo);
}
