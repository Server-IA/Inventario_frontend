package com.coagronet.tipoMedicion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.tipoMedicion.TipoMedicion;
import com.coagronet.tipoMedicion.dtos.TipoMedicionDTO;

@Mapper(componentModel = "spring")
public interface TipoMedicionMapper {
    @Mapping(source = "unidad.id", target = "unidadId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target ="empresaId")
    TipoMedicionDTO toDto(TipoMedicion tipoMedicion);

    @Mapping(source = "unidadId", target = "unidad.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    TipoMedicion toEntity(TipoMedicionDTO tipoMedicionDTO);

    @Mapping(source = "unidad.id", target = "unidadId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target ="empresaId", ignore = true)
    TipoMedicionDTO toListDTO(TipoMedicion tipoMedicion);


    
}
