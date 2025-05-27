package com.coagronet.ingrediente.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.ingrediente.Ingrediente;
import com.coagronet.ingrediente.dtos.IngredienteDTO;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {

    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    IngredienteDTO toDTO(Ingrediente ingrediente);

    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    Ingrediente toEntity(IngredienteDTO ingredienteDTO);

    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    IngredienteDTO toListDto(Ingrediente ingrediente);

}
