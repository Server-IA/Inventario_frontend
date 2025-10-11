package com.coagronet.evaluacionitem.mappers;

import com.coagronet.evaluacionitem.EvaluacionItem;
import com.coagronet.evaluacionitem.dtos.EvaluacionItemCreateDTO;
import com.coagronet.evaluacionitem.dtos.EvaluacionItemResponseDTO;
import com.coagronet.evaluacionitem.dtos.EvaluacionItemUpdateDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EvaluacionItemMapper {

    @Mapping(target = "evaluacion", ignore = true)
    @Mapping(target = "criterioEvaluacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    EvaluacionItem toEntity(EvaluacionItemCreateDTO dto);

    @InheritInverseConfiguration
    @Mapping(target = "estadoId", source = "estado.id")
    @Mapping(target = "empresaId", source = "empresa.id")
    @Mapping(target = "evaluacionId", source = "evaluacion.id")
    @Mapping(target = "criterioEvaluacionId", source = "criterioEvaluacion.id")
    EvaluacionItemResponseDTO toResponseDTO(EvaluacionItem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "evaluacion", ignore = true)
    @Mapping(target = "criterioEvaluacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(EvaluacionItemUpdateDTO dto, @MappingTarget EvaluacionItem entity);

}
