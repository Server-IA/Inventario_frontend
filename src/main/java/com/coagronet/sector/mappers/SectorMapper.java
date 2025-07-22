package com.coagronet.sector.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.sector.Sector;
import com.coagronet.sector.dtos.SectorDTO;

@Mapper(componentModel = "spring")
public interface SectorMapper {
    
    @Mapping(source = "subseccion.id", target = "subseccionId")
    @Mapping(source = "variedad.id", target = "variedadId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    SectorDTO toDto (Sector sector);


    @Mapping(source = "subseccionId", target = "subseccion.id")
    @Mapping(source = "variedadId", target = "variedad.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    Sector toEntity (SectorDTO sectorDTO);

    @Mapping(source = "subseccion.id", target = "subseccionId")
    @Mapping(source = "variedad.id", target = "variedadId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    SectorDTO toListDTO (Sector sector);
}
