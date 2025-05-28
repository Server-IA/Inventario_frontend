package com.coagronet.producto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.coagronet.producto.Producto;
import com.coagronet.producto.dtos.ProductoDTO;

@Component
@Mapper(componentModel = "spring")
public interface ProductoMapper {


    @Mapping(source = "productoCategoria.id", target = "productoCategoriaId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    @Mapping(source = "unidad.id", target = "unidadMinimaId")
    ProductoDTO toDto(Producto producto);

    @Mapping(source = "productoCategoriaId", target = "productoCategoria.id")
    @Mapping(source = "unidadMinimaId", target = "unidad.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    Producto toEntity(ProductoDTO productoDTO);
}
