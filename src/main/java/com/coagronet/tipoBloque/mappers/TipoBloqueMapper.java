package com.coagronet.tipoBloque.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.estado.Estado;
import com.coagronet.tipoBloque.TipoBloque;
import com.coagronet.tipoBloque.dtos.TipoBloqueDTO;
import com.coagronet.tipoBloque.dtos.TipoBloqueMinimalDTO;

@Mapper(componentModel = "spring")
public interface TipoBloqueMapper {

    TipoBloqueMapper INSTANCE = Mappers.getMapper(TipoBloqueMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    @Mapping(source = "empresa.id", target = "empresa")
    TipoBloqueDTO toDTO(TipoBloque tipoBloque);

    TipoBloqueMinimalDTO toMinimalDTO(TipoBloque tipoBloque);

    @Mapping(source = "estado", target = "estado.id")
    @Mapping(source = "empresa", target = "empresa.id")
    TipoBloque toEntity(TipoBloqueDTO tipoBloqueDTO);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget TipoBloque tipoBloque,
            TipoBloqueDTO tipoBloqueDTO) {
        if (tipoBloque.getEstado() == null && tipoBloqueDTO.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(tipoBloqueDTO.getEstado());
            tipoBloque.setEstado(estado);
        }
    }

}
