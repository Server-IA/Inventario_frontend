package com.inventario.inventarioItem.mappers;

import com.inventario.inventarioItem.InventarioItem;
import com.inventario.inventarioItem.dtos.InventarioItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventarioItemMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "inventario.id", target = "inventarioId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	@Mapping(source = "articuloKardex.identificadorProducto", target = "productoIdentificadorId")
	InventarioItemDTO toDTO(InventarioItem inventarioItem);

	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "inventarioId", target = "inventario.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	@Mapping(source = "productoIdentificadorId", target = "articuloKardex.identificadorProducto")
	InventarioItem toEntity(InventarioItemDTO inventarioItemDTO);

}
