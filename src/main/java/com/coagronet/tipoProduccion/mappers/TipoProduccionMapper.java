package com.coagronet.tipoProduccion.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.estado.Estado;
import com.coagronet.tipoProduccion.TipoProduccion;
import com.coagronet.tipoProduccion.dtos.TipoProduccionDTO;

@Mapper(componentModel = "spring")
public interface TipoProduccionMapper {
    TipoProduccionMapper INSTANCE = Mappers.getMapper(TipoProduccionMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    TipoProduccionDTO toDto(TipoProduccion tipoProduccion);

    @Mapping(source = "estado", target = "estado.id")
    TipoProduccion toEntity(TipoProduccionDTO tipoProduccionDTO);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget TipoProduccion tipoProduccion, TipoProduccionDTO dto) {
        if (tipoProduccion.getEstado() == null && dto.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(dto.getEstado());
            tipoProduccion.setEstado(estado);
        }
    }
}
