package com.coagronet.espacioOcupacion.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.espacioOcupacion.EspacioOcupacion;
import com.coagronet.espacioOcupacion.dtos.EspacioOcupacionDTO;
import com.coagronet.estado.Estado;

@Mapper(componentModel = "spring")
public interface EspacioOcupacionMapper {

    EspacioOcupacionMapper INSTANCE = Mappers.getMapper(EspacioOcupacionMapper.class);

    @Mapping(source = "espacio.id", target = "espacio")
    @Mapping(source = "actividadOcupacion.id", target = "actividadOcupacion")
    @Mapping(source = "estado.id", target = "estado")
    EspacioOcupacionDTO toDTO(EspacioOcupacion espacioOcupacion);

    @Mapping(source = "espacio", target = "espacio.id")
    @Mapping(source = "actividadOcupacion", target = "actividadOcupacion.id")
    @Mapping(source = "estado", target = "estado.id")
    EspacioOcupacion toEntity(EspacioOcupacionDTO espacioOcupacionDTO);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget EspacioOcupacion espacioOcupacion,
            EspacioOcupacionDTO espacioOcupacionDTO) {
        if (espacioOcupacion.getEstado() == null && espacioOcupacionDTO.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(espacioOcupacionDTO.getEstado());
            espacioOcupacion.setEstado(estado);
        }
    }

}
