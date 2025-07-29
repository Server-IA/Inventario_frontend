package com.coagronet.programacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.programacion.Programacion;
import com.coagronet.programacion.dtos.ProgramacionDTO;

@Mapper
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
