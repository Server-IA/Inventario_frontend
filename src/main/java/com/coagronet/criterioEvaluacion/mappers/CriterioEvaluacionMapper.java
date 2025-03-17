package com.coagronet.criterioEvaluacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.criterioEvaluacion.CriterioEvaluacion;
import com.coagronet.criterioEvaluacion.dtos.CriterioEvaluacionDTO;

@Mapper(componentModel = "spring")
public interface CriterioEvaluacionMapper {

    CriterioEvaluacionMapper INSTANCE = Mappers.getMapper(CriterioEvaluacionMapper.class);

    @Mapping(source = "tipoEvaluacion.id", target = "tipoEvaluacion")
    @Mapping(source = "estado.id", target = "estado")
    CriterioEvaluacionDTO toDTO(CriterioEvaluacion criterioEvaluacion);

    @Mapping(source = "tipoEvaluacion", target = "tipoEvaluacion.id")
    @Mapping(source = "estado", target = "estado.id")
    CriterioEvaluacion toEntity(CriterioEvaluacionDTO criterioEvaluacionDTO);

}
