package com.coagronet.pedido.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.pedido.Pedido;
import com.coagronet.pedido.dtos.PedidoDTO;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

	@Mapping(source = "almacen.id", target = "almacenId")
	@Mapping(source = "produccion.id", target = "produccionId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	PedidoDTO toDto(Pedido pedido);

	@Mapping(source = "almacenId", target = "almacen.id")
	@Mapping(source = "produccionId", target = "produccion.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	Pedido toEntity(PedidoDTO pedidoDTO);

}
