package com.coagronet.almacen.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.almacen.Almacen;
import com.coagronet.almacen.dtos.AlmacenDTO;
import com.coagronet.almacen.dtos.AlmacenMinimalDTO;
import com.coagronet.estado.Estado;

@Mapper(componentModel = "spring")
public interface AlmacenMapper {

    AlmacenMapper INSTANCE = Mappers.getMapper(AlmacenMapper.class);

    @Mapping(source = "sede.id", target = "sede")
    @Mapping(source = "estado.id", target = "estado")
    AlmacenDTO toDTO(Almacen almacen);

    AlmacenMinimalDTO toMinimalDTO(Almacen almacen);

    @Mapping(source = "sede", target = "sede.id")
    @Mapping(source = "estado", target = "estado.id")
    Almacen toEntity(AlmacenDTO almacenDTO);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget Almacen almacen, AlmacenDTO almacenDTO) {
        if (almacen.getEstado() == null && almacenDTO.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(almacenDTO.getEstado());
            almacen.setEstado(estado);
        }
    }

    default Almacen updateEntityFromDto(AlmacenDTO dto, Almacen existingEntity) {
        if (dto == null || existingEntity == null) {
            return existingEntity;
        }

        // Actualizar campos que pueden cambiar
        existingEntity.setNombre(dto.getNombre());
        existingEntity.setCoordenadas(dto.getCoordenadas());
        existingEntity.setDescripcion(dto.getDescripcion());
        existingEntity.setGeolocalizacion(dto.getGeolocalizacion());
        // Añade otros campos que puedan actualizarse

        return existingEntity;
    }

}
