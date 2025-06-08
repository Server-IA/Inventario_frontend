package com.coagronet.espacioOcupacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.espacioOcupacion.EspacioOcupacion;
import com.coagronet.espacioOcupacion.dtos.EspacioOcupacionDTO;

@Mapper(componentModel = "spring")
public interface EspacioOcupacionMapper {

    @Mapping(source = "espacio.id", target = "espacioId")
    @Mapping(source = "actividadOcupacion.id", target = "actividadOcupacionId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    EspacioOcupacionDTO toDTO(EspacioOcupacion espacioOcupacion);

    @Mapping(source = "espacioId", target = "espacio.id")
    @Mapping(source = "actividadOcupacionId", target = "actividadOcupacion.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    EspacioOcupacion toEntity(EspacioOcupacionDTO espacioOcupacionDTO);



}
