package com.coagronet.estado.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.coagronet.estado.Estado;
import com.coagronet.estado.dtos.EstadoDTO;

@Mapper(componentModel = "spring")
public interface EstadoMapper {
    EstadoDTO toDTO(Estado estado);

    Estado toEntity(EstadoDTO estadoDTO);

    @Mapping(target = "descripcion", ignore = true)
    @Mapping(target = "acronimo", ignore = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    EstadoDTO toShortDTO(Estado estado);
}