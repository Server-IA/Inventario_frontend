package com.coagronet.bloque.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.bloque.Bloque;
import com.coagronet.bloque.dtos.BloqueDTO;
import com.coagronet.bloque.dtos.BloqueMinimalDTO;
import com.coagronet.estado.Estado;

@Mapper(componentModel = "spring")
public interface BloqueMapper {

    BloqueMapper INSTANCE = Mappers.getMapper(BloqueMapper.class);

    @Mapping(source = "sede.id", target = "sede")
    @Mapping(source = "tipoBloque.id", target = "tipoBloque")
    @Mapping(source = "estado.id", target = "estado")
    BloqueDTO toDTO(Bloque bloque);

    BloqueMinimalDTO toMinimalDTO(Bloque bloque);

    @Mapping(source = "sede", target = "sede.id")
    @Mapping(source = "tipoBloque", target = "tipoBloque.id")
    @Mapping(source = "estado", target = "estado.id")
    Bloque toEntity(BloqueDTO bloqueDTO);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget Bloque bloque, BloqueDTO bloqueDTO) {
        if (bloque.getEstado() == null && bloqueDTO.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(bloqueDTO.getEstado());
            bloque.setEstado(estado);
        }
    }

}
