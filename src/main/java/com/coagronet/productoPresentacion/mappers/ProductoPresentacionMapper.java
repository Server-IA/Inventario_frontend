package com.coagronet.productoPresentacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.productoPresentacion.ProductoPresentacion;
import com.coagronet.productoPresentacion.dtos.ProductoPresentacionDTO;

@Mapper(componentModel = "spring")
public interface ProductoPresentacionMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "unidad.id", target = "unidadId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "marca.id", target = "marcaId")
    @Mapping(source = "presentacion.id", target = "presentacionId")
    @Mapping(source = "empresa.id", target = "empresaId")
    ProductoPresentacionDTO toDto(ProductoPresentacion productoPresentacion);


    @Mapping(target = "productoId", ignore = true)
    @Mapping(target = "unidadId", ignore = true)
    @Mapping(target = "estadoId", ignore = true)
    @Mapping(target = "marcaId", ignore = true)
    @Mapping(target = "presentacionId", ignore = true)
    @Mapping(target = "empresaId", ignore = true)
    @Mapping(target = "ingredienteId", ignore = true)
    ProductoPresentacionDTO toMinimalDTO(ProductoPresentacion productoPresentacion);

    @Mapping(source = "productoId", target = "producto.id")
    @Mapping(source = "unidadId", target = "unidad.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "marcaId", target = "marca.id")
    @Mapping(source = "presentacionId", target = "presentacion.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    ProductoPresentacion toEntity(ProductoPresentacionDTO productoPresentacionDTO);
}
