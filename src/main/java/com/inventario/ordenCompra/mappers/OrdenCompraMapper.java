package com.inventario.ordenCompra.mappers;

import com.inventario.ordenCompra.dtos.OrdenCompraCreateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.ordenCompra.OrdenCompra;
import com.inventario.ordenCompra.dtos.OrdenCompraDTO;

@Mapper(componentModel = "spring")
public interface OrdenCompraMapper {

	@Mapping(source = "pedido.id", target = "pedidoId")
	@Mapping(source = "proveedor.id", target = "proveedorId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	OrdenCompraDTO toDTO(OrdenCompra ordenCompra);

	@Mapping(source = "pedidoId", target = "pedido.id")
	@Mapping(source = "proveedorId", target = "proveedor.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	OrdenCompra toEntity(OrdenCompraDTO ordenCompraDTO);

	//registro
	@Mapping(source = "pedidoId", target = "pedido.id")
	@Mapping(source = "proveedorId", target = "proveedor.id")
	OrdenCompra toEntity(OrdenCompraCreateDTO ordenCompraCreateDTO);

}
