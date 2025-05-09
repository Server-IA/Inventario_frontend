package com.coagronet.grupo.mappers;


import com.coagronet.grupo.Grupo;
import com.coagronet.grupo.dtos.GrupoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")

public interface GrupoMapper {

    GrupoMapper INSTANCE = Mappers.getMapper(GrupoMapper.class);

    @Mapping(source = "empresa.id", target = "empresa")
    @Mapping(source = "estado.id", target = "estado")
    GrupoDTO toDTO(Grupo grupo);

    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "descripcion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    GrupoDTO toMinimalDTO(Grupo grupo);

    @Mapping(source = "empresa", target = "empresa.id")
    @Mapping(source = "estado", target = "estado.id")
    Grupo toEntity(GrupoDTO grupoDTO);

}
