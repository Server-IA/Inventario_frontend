package com.inventario.fase.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.fase.Fase;
import com.inventario.fase.dtos.FaseDTO;

@Mapper(componentModel = "spring")
public interface FaseMapper {
    @Mapping(source = "tipoFase.id", target = "tipoFaseId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    FaseDTO toDto(Fase fase);

    @Mapping(source = "tipoFaseId", target = "tipoFase.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    Fase toEntity(FaseDTO faseDTO);

     @Mapping(source = "tipoFase.id", target = "tipoFaseId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    FaseDTO toListDTO(Fase fase);

    
}