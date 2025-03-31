package com.coagronet.departamento.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.departamento.Departamento;
import com.coagronet.departamento.dtos.DepartamentoDTO;

@Mapper(componentModel = "spring")
public interface DepartamentoMapper {

    @Mapping(source = "nombre", target="name")
    DepartamentoDTO toDTO(Departamento departamento);
}
