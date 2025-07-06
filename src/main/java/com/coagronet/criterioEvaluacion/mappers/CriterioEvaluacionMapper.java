package com.coagronet.criterioEvaluacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.coagronet.criterioEvaluacion.CriterioEvaluacion;
import com.coagronet.criterioEvaluacion.dtos.CriterioEvaluacionDTO;

@Mapper(componentModel = "spring")
public interface CriterioEvaluacionMapper {

    @Mapping(source = "tipoEvaluacion.id", target = "tipoEvaluacionId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    CriterioEvaluacionDTO toDTO(CriterioEvaluacion criterioEvaluacion);

    @Mapping(source = "tipoEvaluacionId", target = "tipoEvaluacion.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    CriterioEvaluacion toEntity(CriterioEvaluacionDTO criterioEvaluacionDTO);

    @Mapping(source = "tipoEvaluacion.id", target = "tipoEvaluacionId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    CriterioEvaluacionDTO toListDTO(CriterioEvaluacion criterioEvaluacion);
}
