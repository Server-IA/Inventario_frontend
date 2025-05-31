package com.coagronet.articuloOrdenCompra.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.articuloOrdenCompra.ArticuloOrdenCompra;
import com.coagronet.articuloOrdenCompra.dtos.ArticuloOrdenCompraDTO;

@Mapper(componentModel = "spring")
public interface ArticuloOrdenCompraMapper {

    @Mapping(source = "ordenCompra.id", target = "ordenCompraId")
    @Mapping(source = "productoPresentacion.id", target = "productoPresentacionId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    ArticuloOrdenCompraDTO toDTO(ArticuloOrdenCompra articuloOrdenCompra);

    @Mapping(source = "ordenCompraId", target = "ordenCompra.id")
    @Mapping(source = "productoPresentacionId", target = "productoPresentacion.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    ArticuloOrdenCompra toEntity(ArticuloOrdenCompraDTO articuloOrdenCompraDTO);

    @Mapping(source = "ordenCompra.id", target = "ordenCompraId")
    @Mapping(source = "productoPresentacion.id", target = "productoPresentacionId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    ArticuloOrdenCompraDTO toListDTO(ArticuloOrdenCompra articuloOrdenCompra);

}