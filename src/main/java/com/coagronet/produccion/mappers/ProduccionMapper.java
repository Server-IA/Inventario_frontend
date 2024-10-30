package com.coagronet.produccion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.produccion.Produccion;
import com.coagronet.produccion.dtos.DTOProduccion;

@Mapper(componentModel = "spring")
public interface ProduccionMapper {
    
    ProduccionMapper INSTANCE = Mappers.getMapper(ProduccionMapper.class);
    
    @Mapping(source = "tipoProduccion.id", target = "tipoProduccion")
    @Mapping(source = "espacio.id", target = "espacio")
    @Mapping(source = "estado.id", target = "estado")
    DTOProduccion toDto(Produccion produccion);

    @Mapping(source = "tipoProduccion", target = "tipoProduccion.id")
    @Mapping(source = "espacio", target = "espacio.id")
    @Mapping(source = "estado", target = "estado.id")
    Produccion toEntity(DTOProduccion dtoProduccion);
}

