package com.coagronet.tipoMovimiento.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.estado.Estado;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.tipoMovimiento.dtos.TipoMovimientoDTO;
import com.coagronet.tipoMovimiento.dtos.TipoMovimientoMinimalDTO;

@Mapper(componentModel = "spring")
public interface TipoMovimientoMapper {
    TipoMovimientoMapper INSTANCE = Mappers.getMapper(TipoMovimientoMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    TipoMovimientoDTO toDto(TipoMovimiento tipoMovimiento);

    // Nuevo método para mapear a DTO minimal
    TipoMovimientoMinimalDTO toMinimalDto(TipoMovimiento tipoMovimiento);

    @Mapping(source = "estado", target = "estado.id")
    TipoMovimiento toEntity(TipoMovimientoDTO tipoMovimientoDTO);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget TipoMovimiento tipoMovimiento, TipoMovimientoDTO dto) {
        if (tipoMovimiento.getEstado() == null && dto.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(dto.getEstado());
            tipoMovimiento.setEstado(estado);
        }
    }
}
