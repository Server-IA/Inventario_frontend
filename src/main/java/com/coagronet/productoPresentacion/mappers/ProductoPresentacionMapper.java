package com.coagronet.productoPresentacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.productoPresentacion.ProductoPresentacion;
import com.coagronet.productoPresentacion.dtos.ProductoPresentacionDTO;
import com.coagronet.productoPresentacion.dtos.ProductoPresentacionMinimalDTO;

@Mapper(componentModel = "spring")
public interface ProductoPresentacionMapper {

    ProductoPresentacionMapper INSTANCE = Mappers.getMapper(ProductoPresentacionMapper.class);

    @Mapping(source = "producto.id", target = "producto")
    @Mapping(source = "unidad.id", target = "unidad")
    @Mapping(source = "estado.id", target = "estado")
    @Mapping(source = "marca.id", target = "marca")
    @Mapping(source = "presentacion.id", target = "presentacion")
    ProductoPresentacionDTO toDto(ProductoPresentacion productoPresentacion);

    ProductoPresentacionMinimalDTO toMinimalDTO(ProductoPresentacion productoPresentacion);

    @Mapping(source = "producto", target = "producto.id")
    @Mapping(source = "unidad", target = "unidad.id")
    @Mapping(source = "estado", target = "estado.id")
    @Mapping(source = "marca", target = "marca.id")
    @Mapping(source = "presentacion", target = "presentacion.id")
    ProductoPresentacion toEntity(ProductoPresentacionDTO productoPresentacionDTO);
}
