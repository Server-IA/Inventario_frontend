package com.coagronet.tipoSede.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.tipoSede.TipoSede;
import com.coagronet.tipoSede.dtos.TipoSedeDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TipoSedeMapper {

    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    TipoSedeDTO toDTO(TipoSede tipoSede);


    @Mapping(target = "empresaId", ignore = true)
    @Mapping(source = "estado.id", target = "estadoId")
    TipoSedeDTO toListDTO(TipoSede tipoSede);


    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    TipoSede toEntity(TipoSedeDTO tipoSedeDTO);


}
