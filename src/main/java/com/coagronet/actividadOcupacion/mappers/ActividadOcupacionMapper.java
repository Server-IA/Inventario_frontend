package com.coagronet.actividadOcupacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.coagronet.actividadOcupacion.ActividadOcupacion;
import com.coagronet.actividadOcupacion.dtos.ActividadOcupacionMinimalDTO;

@Mapper(componentModel = "spring")
public interface ActividadOcupacionMapper {

    ActividadOcupacionMapper INSTANCE = Mappers.getMapper(ActividadOcupacionMapper.class);

    ActividadOcupacionMinimalDTO toMinimalDTO(ActividadOcupacion actividadOcupacion);

}
