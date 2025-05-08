package com.coagronet.bloque.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.bloque.Bloque;
import com.coagronet.bloque.dtos.BloqueDTO;
import com.coagronet.estado.Estado;

@Mapper(componentModel = "spring")
public interface BloqueMapper {

    BloqueMapper INSTANCE = Mappers.getMapper(BloqueMapper.class);

    @Mapping(source = "sede.id", target = "sedeId")
    @Mapping(source = "tipoBloque.id", target = "tipoBloqueId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    BloqueDTO toDTO(Bloque bloque);

    @Mapping(target = "sedeId", ignore = true)
    @Mapping(target = "tipoBloqueId", ignore = true)
    @Mapping(target = "numeroPisos", ignore = true)
    @Mapping(target = "descripcion", ignore = true)
    @Mapping(target = "estadoId", ignore = true)
    @Mapping(target = "geolocalizacion", ignore = true)
    @Mapping(target = "coordenadas", ignore = true)
    @Mapping(target = "direccion", ignore = true)
    @Mapping(target = "empresaId", ignore = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    BloqueDTO toMinimalDTO(Bloque bloque);

    @Mapping(source = "sedeId", target = "sede.id")
    @Mapping(source = "tipoBloqueId", target = "tipoBloque.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    Bloque toEntity(BloqueDTO bloqueDTO);

    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget Bloque bloque, BloqueDTO bloqueDTO) {
        if (bloque.getEstado() == null && bloqueDTO.getEstadoId() != null) {
            Estado estado = new Estado();
            estado.setId(bloqueDTO.getEstadoId());
            bloque.setEstado(estado);
        }
    }

}
