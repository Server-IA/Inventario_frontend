package com.coagronet.unidad.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.estado.Estado;
import com.coagronet.unidad.Unidad;
import com.coagronet.unidad.dtos.UnidadDTO;

@Mapper(componentModel = "spring")
public interface UnidadMapper {

    UnidadMapper INSTANCE = Mappers.getMapper(UnidadMapper.class);

    @Mapping(source = "empresa.id", target = "empresa")
    @Mapping(source = "estado.id", target = "estado")
    UnidadDTO toDTO(Unidad unidad);

    @Mapping(source = "empresa", target = "empresa.id")
    @Mapping(source = "estado", target = "estado.id")
    Unidad toEntity(UnidadDTO unidadDTO);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget Unidad unidad,
            UnidadDTO unidadDTO) {
        if (unidad.getEstado() == null && unidadDTO.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(unidadDTO.getEstado());
            unidad.setEstado(estado);
        }
    }

}
