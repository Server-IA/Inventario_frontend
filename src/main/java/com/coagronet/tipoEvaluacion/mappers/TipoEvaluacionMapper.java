package com.coagronet.tipoEvaluacion.mappers;

import com.coagronet.tipoEvaluacion.TipoEvaluacion;
import com.coagronet.tipoEvaluacion.dtos.TipoEvaluacionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TipoEvaluacionMapper {

    TipoEvaluacionMapper INSTANCE = Mappers.getMapper(TipoEvaluacionMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    TipoEvaluacionDTO toDTO(TipoEvaluacion tipoEvaluacion);

    @Mapping(source = "estado", target = "estado.id")
    TipoEvaluacion toEntity(TipoEvaluacionDTO tipoEvaluacionDTO);
}
