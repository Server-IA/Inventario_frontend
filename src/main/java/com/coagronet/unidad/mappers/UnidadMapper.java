package com.coagronet.unidad.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.coagronet.unidad.Unidad;
import com.coagronet.unidad.dtos.UnidadDTO;

@Mapper(componentModel = "spring")
public interface UnidadMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(source = "estado.id", target = "estadoId")
    UnidadDTO toDTO(Unidad unidad);

    @Mapping(source = "empresaId", target = "empresa.id")
    @Mapping(source = "estadoId", target = "estado.id")
    Unidad toEntity(UnidadDTO unidadDTO);

}
