package com.coagronet.articuloPedido.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.articuloPedido.ArticuloPedido;
import com.coagronet.articuloPedido.dtos.ArticuloPedidoDTO;

@Mapper(componentModel = "spring")
public interface ArticuloPedidoMapper {

	@Mapping(source = "pedido.id", target = "pedidoId")
	@Mapping(source = "presentacionProducto.id", target = "presentacionProductoId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	ArticuloPedidoDTO toDTO(ArticuloPedido articuloPedido);

	@Mapping(source = "pedidoId", target = "pedido.id")
	@Mapping(source = "presentacionProductoId", target = "presentacionProducto.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	ArticuloPedido toEntity(ArticuloPedidoDTO articuloPedidoDTO);

	@Mapping(source = "pedido.id", target = "pedidoId")
	@Mapping(source = "presentacionProducto.id", target = "presentacionProductoId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	ArticuloPedidoDTO toListDTO(ArticuloPedido articuloPedido);

}
