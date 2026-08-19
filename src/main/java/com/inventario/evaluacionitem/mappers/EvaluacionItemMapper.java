package com.inventario.evaluacionitem.mappers;

import com.inventario.evaluacionitem.EvaluacionItem;
import com.inventario.evaluacionitem.dtos.EvaluacionItemCreateDTO;
import com.inventario.evaluacionitem.dtos.EvaluacionItemResponseDTO;
import com.inventario.evaluacionitem.dtos.EvaluacionItemUpdateDTO;
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
