package com.coagronet.productoCategoria.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.productoCategoria.ProductoCategoria;
import com.coagronet.productoCategoria.dtos.ProductoCategoriaDTO;

@Mapper(componentModel = "spring")
public interface ProductoCategoriaMapper {
    ProductoCategoriaMapper INSTANCE = Mappers.getMapper(ProductoCategoriaMapper.class);

    @Mapping(source = "estado.id", target = "estado")
    @Mapping(source = "empresa.id", target = "empresa")
    ProductoCategoriaDTO toDTO(ProductoCategoria productoCategoria);

    @Mapping(source = "estado", target = "estado.id")
    @Mapping(source = "empresa", target = "empresa.id")
    ProductoCategoria toEntity(ProductoCategoriaDTO productoCategoriaDTO);
}
