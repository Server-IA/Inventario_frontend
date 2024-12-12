package com.coagronet.producto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.coagronet.producto.Producto;
import com.coagronet.producto.dtos.ProductoDTO;
import com.coagronet.producto.dtos.ProductoMinimalDTO;

@Component
@Mapper(componentModel = "spring")
public interface ProductoMapper {

    ProductoMapper INSTANCE = Mappers.getMapper(ProductoMapper.class);

    @Mapping(source = "productoCategoria.id", target = "productoCategoria")
    @Mapping(source = "estado.id", target = "estado")
    @Mapping(source = "empresa.id", target = "empresa")
    ProductoDTO toDto(Producto producto);

    ProductoMinimalDTO toMinimalDTO(Producto producto);

    @Mapping(source = "productoCategoria", target = "productoCategoria.id")
    @Mapping(source = "estado", target = "estado.id")
    @Mapping(source = "empresa", target = "empresa.id")
    Producto toEntity(ProductoDTO productoDTO);
}
