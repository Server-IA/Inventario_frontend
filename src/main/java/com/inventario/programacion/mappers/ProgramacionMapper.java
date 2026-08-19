package com.inventario.programacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.programacion.Programacion;
import com.inventario.programacion.dtos.ProgramacionDTO;

@Mapper(componentModel = "spring")
public interface ProgramacionMapper {
    @Mapping(source = "subseccionDispositivo.id", target = "subseccionDispositivoId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    ProgramacionDTO toDto(Programacion programacion);

    @Mapping(source = "subseccionDispositivoId", target = "subseccionDispositivo.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    Programacion toEntity(ProgramacionDTO programacionDTO);

    @Mapping(source = "subseccionDispositivo.id", target = "subseccionDispositivoId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    ProgramacionDTO toListDTO(Programacion programacion);
    
}
