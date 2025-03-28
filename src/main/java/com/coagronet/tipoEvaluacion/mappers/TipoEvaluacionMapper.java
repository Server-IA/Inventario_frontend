package com.coagronet.tipoEvaluacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.tipoEvaluacion.TipoEvaluacion;
import com.coagronet.tipoEvaluacion.dtos.TipoEvaluacionDTO;

@Mapper(componentModel = "spring")
public interface TipoEvaluacionMapper{

    @Mapping(source = "estado.id", target = "estadoId")
    TipoEvaluacionDTO toDTO(TipoEvaluacion tipoEvaluacion);

    @Mapping(source = "estadoId", target = "estado.id")
    TipoEvaluacion toEntity(TipoEvaluacionDTO tipoEvaluacionDTO);
}
