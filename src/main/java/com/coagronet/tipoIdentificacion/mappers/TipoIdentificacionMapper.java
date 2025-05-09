package com.coagronet.tipoIdentificacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.tipoIdentificacion.TipoIdentificacion;
import com.coagronet.tipoIdentificacion.dtos.TipoIdentificacionDTO;

@Mapper(componentModel = "spring")
public interface TipoIdentificacionMapper {

    TipoIdentificacionMapper INSTANCE = Mappers.getMapper(TipoIdentificacionMapper.class);

    @Mapping(source = "estado.id", target = "estadoId")
    TipoIdentificacionDTO toDTO(TipoIdentificacion tipoIdentificacion);

    @Mapping(source = "estadoId", target = "estado.id")
    TipoIdentificacion toEntity(TipoIdentificacionDTO tipoIdentificacionDTO);

}
