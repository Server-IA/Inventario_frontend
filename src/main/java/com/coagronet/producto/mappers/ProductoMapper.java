package com.coagronet.producto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.coagronet.producto.Producto;
import com.coagronet.producto.dtos.ProductoDTO;

@Component
@Mapper(componentModel = "spring")
public interface ProductoMapper {

	@Mapping(source = "productoCategoria.id", target = "productoCategoriaId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	@Mapping(source = "unidad.id", target = "unidadMinimaId")
	@Mapping(source = "ingredientePresentacionProducto.id", target = "ingredientePresentacionProductoId")
	ProductoDTO toDto(Producto producto);

	@Mapping(source = "productoCategoriaId", target = "productoCategoria.id")
	@Mapping(source = "unidadMinimaId", target = "unidad.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	@Mapping(source = "ingredientePresentacionProductoId", target = "ingredientePresentacionProducto.id")
	Producto toEntity(ProductoDTO productoDTO);

}
