package com.coagronet.pais.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.coagronet.pais.Pais;
import com.coagronet.pais.dtos.PaisDTO;

@Mapper(componentModel = "spring")
public interface PaisMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(source = "estado.id", target = "estadoId")
    PaisDTO toDTO(Pais pais);

    @Mapping(source = "empresaId", target = "empresa.id")
    @Mapping(source = "estadoId", target = "estado.id")
    Pais toEntity(PaisDTO paisDTO);

}
