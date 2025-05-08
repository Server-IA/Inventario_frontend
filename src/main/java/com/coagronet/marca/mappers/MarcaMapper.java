package com.coagronet.marca.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.coagronet.estado.Estado;
import com.coagronet.marca.Marca;
import com.coagronet.marca.dtos.MarcaDTO;

@Mapper(componentModel = "spring")
public interface MarcaMapper {

    MarcaMapper INSTANCE = Mappers.getMapper(MarcaMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    @Mapping(source = "empresa.id", target = "empresa")
    MarcaDTO toDTO(Marca marca);


    @Mapping(source = "estado", target = "estado.id")
    @Mapping(source = "empresa", target = "empresa.id")
    Marca toEntity(MarcaDTO marcaDTO);


    @Mapping(target = "descripcion", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    MarcaDTO toMinimalDTO(Marca marca);


    @AfterMapping
    default void setEstadoAfterMapping(@MappingTarget Marca marca,
            MarcaDTO marcaDTO) {
        if (marca.getEstado() == null && marcaDTO.getEstado() != null) {
            Estado estado = new Estado();
            estado.setId(marcaDTO.getEstado());
            marca.setEstado(estado);
        }
    }

}
