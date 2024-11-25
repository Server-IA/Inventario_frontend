package com.coagronet.sede.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.estado.Estado;
import com.coagronet.sede.Sede;
import com.coagronet.sede.dtos.SedeDTO;
import com.coagronet.sede.dtos.SedeMinimalDTO;

@Mapper(componentModel = "spring")
public interface SedeMapper {

    SedeMapper INSTANCE = Mappers.getMapper(SedeMapper.class);

    @Mapping(source = "grupo.id", target = "grupo")
    @Mapping(source = "tipoSede.id", target = "tipoSede")
    @Mapping(source = "empresa.id", target = "empresa")
    @Mapping(source = "municipio.id", target = "municipio")
    @Mapping(source = "estado.id", target = "estado")
    SedeDTO toDto(Sede sede);

    @Mapping(source = "grupo", target = "grupo.id")
    @Mapping(source = "tipoSede", target = "tipoSede.id")
    @Mapping(source = "empresa", target = "empresa.id")
    @Mapping(source = "municipio", target = "municipio.id")
    @Mapping(source = "estado", target = "estado.id")
    Sede toEntity(SedeDTO dto);

    SedeMinimalDTO toMinimalDTO(Sede sede);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget Sede sede, SedeDTO sedeDTO) {
        if (sede.getEstado() == null && sedeDTO.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(sedeDTO.getEstado());
            sede.setEstado(estado);
        }
    }

}