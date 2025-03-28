package com.coagronet.tipoEvaluacion.mappers;

import com.coagronet.tipoEvaluacion.TipoEvaluacion;
import com.coagronet.tipoEvaluacion.dtos.TipoEvaluacionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.extensions.spring.SpringMapperConfig;
import org.springframework.core.convert.converter.Converter;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SpringMapperConfig
public interface TipoEvaluacionMapper extends Converter<TipoEvaluacion, TipoEvaluacionDTO> {

    @Override
    @Mapping(source = "estado.id", target = "estadoId")
    TipoEvaluacionDTO convert(TipoEvaluacion tipoEvaluacion);

    @Mapping(source = "estadoId", target = "estado.id")
    TipoEvaluacion toEntity(TipoEvaluacionDTO tipoEvaluacionDTO);
}
