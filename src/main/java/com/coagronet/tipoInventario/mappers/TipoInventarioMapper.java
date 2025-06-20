package com.coagronet.tipoInventario.mappers;

import com.coagronet.tipoInventario.TipoInventario;
import com.coagronet.tipoInventario.dtos.TipoInventarioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TipoInventarioMapper {

    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    TipoInventarioDTO toDTO(TipoInventario tipoInventario);

    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    TipoInventario toEntity(TipoInventarioDTO tipoInventarioDTO);
}
