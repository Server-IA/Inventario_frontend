package com.coagronet.articuloInventario.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.articuloInventario.ArticuloInventario;
import com.coagronet.articuloInventario.dtos.ArticuloInventarioDTO;

@Mapper(componentModel = "spring")
public interface ArticuloInventarioMapper {

    @Mapping(source = "inventario.id", target = "inventarioId")
    @Mapping(source = "articuloKardex.identificadorProducto", target = "identificadorProducto")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    ArticuloInventarioDTO toDTO(ArticuloInventario articuloInventario);

    @Mapping(source = "inventarioId", target = "inventario.id")
    @Mapping(source = "identificadorProducto", target = "articuloKardex.identificadorProducto")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    ArticuloInventario toEntity(ArticuloInventarioDTO articuloInventarioDTO);

}
