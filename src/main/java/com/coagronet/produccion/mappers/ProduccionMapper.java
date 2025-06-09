package com.coagronet.produccion.mappers;

import com.coagronet.produccion.dtos.ProduccionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.produccion.Produccion;

@Mapper(componentModel = "spring")
public interface ProduccionMapper {

    @Mapping(source = "tipoProduccion.id", target = "tipoProduccionId")
    @Mapping(source = "espacio.id", target = "espacioId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    ProduccionDTO toDto(Produccion produccion);

    @Mapping(source = "tipoProduccionId", target = "tipoProduccion.id")
    @Mapping(source = "espacioId", target = "espacio.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "productoId", target = "producto.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    Produccion toEntity(ProduccionDTO produccionDTO);
}
