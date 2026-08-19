package com.inventario.producto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.producto.Producto;
import com.inventario.producto.dtos.ProductoRequestDTO;
import com.inventario.producto.dtos.ProductoResponseDTO;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

	@Mapping(target = "productoCategoriaId", source = "productoCategoria.id")
	@Mapping(target = "productoCategoriaNombre", source = "productoCategoria.nombre")
	@Mapping(target = "estadoId", source = "estado.id")
	@Mapping(target = "estadoNombre", source = "estado.nombre")
	@Mapping(target = "unidadMinimaId", source = "unidadMinima.id")
	@Mapping(target = "unidadMinimaNombre", source = "unidadMinima.nombre")
	ProductoResponseDTO toDto(Producto producto);

	@Mapping(target = "productoCategoria.id", source = "productoCategoriaId")
	@Mapping(target = "estado.id", source = "estadoId")
	@Mapping(target = "empresa.id", source = "empresaId")
	@Mapping(target = "unidadMinima.id", source = "unidadMinimaId")
	Producto toEntity(ProductoRequestDTO productoRequestDTO);

}
