package com.inventario.articuloOrdenCompra.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.articuloOrdenCompra.ArticuloOrdenCompra;
import com.inventario.articuloOrdenCompra.dtos.ArticuloOrdenCompraDTO;

@Mapper(componentModel = "spring")
public interface ArticuloOrdenCompraMapper {

	@Mapping(source = "ordenCompra.id", target = "ordenCompraId")
	@Mapping(source = "presentacionProducto.id", target = "presentacionProductoId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	ArticuloOrdenCompraDTO toDTO(ArticuloOrdenCompra articuloOrdenCompra);

	@Mapping(source = "ordenCompraId", target = "ordenCompra.id")
	@Mapping(source = "presentacionProductoId", target = "presentacionProducto.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	ArticuloOrdenCompra toEntity(ArticuloOrdenCompraDTO articuloOrdenCompraDTO);

	@Mapping(source = "ordenCompra.id", target = "ordenCompraId")
	@Mapping(source = "presentacionProducto.id", target = "presentacionProductoId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	ArticuloOrdenCompraDTO toListDTO(ArticuloOrdenCompra articuloOrdenCompra);

}