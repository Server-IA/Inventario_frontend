package com.coagronet.tipoProduccion.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.estado.Estado;
import com.coagronet.tipoProduccion.TipoProduccion;
import com.coagronet.tipoProduccion.dtos.TipoProduccionDTO;
import com.coagronet.tipoProduccion.dtos.TipoProduccionMinimalDTO;

@Mapper(componentModel = "spring")
public interface TipoProduccionMapper {

    TipoProduccionMapper INSTANCE = Mappers.getMapper(TipoProduccionMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    @Mapping(source = "empresa.id", target = "empresa")
    TipoProduccionDTO toDto(TipoProduccion tipoProduccion);

    TipoProduccionMinimalDTO toMinimalDTO(TipoProduccion tipoProduccion);

    @Mapping(source = "estado", target = "estado.id")
    @Mapping(source = "empresa", target = "empresa.id")
    TipoProduccion toEntity(TipoProduccionDTO tipoProduccionDTO);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget TipoProduccion tipoProduccion,
            TipoProduccionDTO tipoProduccionDTO) {
        if (tipoProduccion.getEstado() == null && tipoProduccionDTO.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(tipoProduccionDTO.getEstado());
            tipoProduccion.setEstado(estado);
        }
    }
}
