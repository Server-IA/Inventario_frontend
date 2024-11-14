package com.coagronet.tipoBloque.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.tipoBloque.TipoBloque;
import com.coagronet.tipoBloque.dtos.TipoBloqueDTO;

@Mapper(componentModel = "spring")
public interface TipoBloqueMapper {

    TipoBloqueMapper INSTANCE = Mappers.getMapper(TipoBloqueMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    TipoBloqueDTO toDTO(TipoBloque tipoBloque);

    @Mapping(source = "estado", target = "estado.id")
    TipoBloque toEntity(TipoBloqueDTO tipoBloqueDTO);

}
