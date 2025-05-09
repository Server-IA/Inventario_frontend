package com.coagronet.departamento.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.departamento.Departamento;
import com.coagronet.departamento.dtos.DepartamentoDTO;

@Mapper(componentModel = "spring")
public interface DepartamentoMapper {

    DepartamentoMapper INSTANCE = Mappers.getMapper(DepartamentoMapper.class);

    @Mapping(source = "pais.id", target = "paisId")
    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(source = "estado.id", target = "estadoId")
    DepartamentoDTO toDTO(Departamento departamento);

    @Mapping(source = "paisId", target = "pais.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    @Mapping(source = "estadoId", target = "estado.id")
    Departamento toEntity(DepartamentoDTO departamentoDTO);

}
