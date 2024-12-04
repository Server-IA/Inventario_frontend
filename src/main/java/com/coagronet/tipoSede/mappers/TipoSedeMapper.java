package com.coagronet.tipoSede.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.estado.Estado;
import com.coagronet.tipoSede.TipoSede;
import com.coagronet.tipoSede.dtos.TipoSedeDTO;
import com.coagronet.tipoSede.dtos.TipoSedeMinimalDTO;

@Mapper(componentModel = "spring")
public interface TipoSedeMapper {

    TipoSedeMapper INSTANCE = Mappers.getMapper(TipoSedeMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    @Mapping(source = "empresa.id", target = "empresa")
    TipoSedeDTO toDTO(TipoSede tipoSede);

    TipoSedeMinimalDTO toMinimalDTO(TipoSede tipoSede);

    @Mapping(source = "estado", target = "estado.id")
    @Mapping(source = "empresa", target = "empresa.id")
    TipoSede toEntity(TipoSedeDTO tipoSedeDTO);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget TipoSede tipoSede,
            TipoSedeDTO tipoSedeDTO) {
        if (tipoSede.getEstado() == null && tipoSedeDTO.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(tipoSedeDTO.getEstado());
            tipoSede.setEstado(estado);
        }
    }

}
