package com.coagronet.tipoSede.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.tipoSede.TipoSede;
import com.coagronet.tipoSede.dtos.TipoSedeDTO;

@Mapper(componentModel = "spring")
public interface TipoSedeMapper {

    TipoSedeMapper INSTANCE = Mappers.getMapper(TipoSedeMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    TipoSedeDTO toDTO(TipoSede tipoSede);

    @Mapping(source = "estado", target = "estado.id")
    TipoSede toEntity(TipoSedeDTO tipoSedeDTO);

}
