package com.inventario.presentacionProducto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.presentacionProducto.PresentacionProducto;
import com.inventario.presentacionProducto.dtos.PresentacionProductoDTO;

@Mapper(componentModel = "spring")
public interface PresentacionProductoMapper {

	@Mapping(source = "producto.id", target = "productoId")
	@Mapping(source = "unidad.id", target = "unidadId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "marca.id", target = "marcaId")
	@Mapping(source = "presentacion.id", target = "presentacionId")
	@Mapping(target = "empresaId", ignore = true)
	PresentacionProductoDTO toDto(PresentacionProducto presentacionProducto);

	@Mapping(source = "productoId", target = "producto.id")
	@Mapping(source = "unidadId", target = "unidad.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "marcaId", target = "marca.id")
	@Mapping(source = "presentacionId", target = "presentacion.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	PresentacionProducto toEntity(PresentacionProductoDTO presentacionProductoDTO);

}
