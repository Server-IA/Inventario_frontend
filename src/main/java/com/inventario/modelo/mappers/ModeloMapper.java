package com.inventario.modelo.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.modelo.Modelo;
import com.inventario.modelo.dtos.ModeloDTO;

@Mapper(componentModel = "spring")
public interface ModeloMapper {
    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "tipoModelo.id", target = "tipoModeloId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    ModeloDTO toDto(Modelo modelo);

    @Mapping(source = "productoId", target = "producto.id")
    @Mapping(source = "tipoModeloId", target = "tipoModelo.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    Modelo toEntity(ModeloDTO modeloDTO);

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "tipoModelo.id", target = "tipoModeloId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore=true)
    ModeloDTO toListDTO(Modelo modelo);

}
