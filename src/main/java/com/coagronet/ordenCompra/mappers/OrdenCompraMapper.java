package com.coagronet.ordenCompra.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.ordenCompra.dtos.OrdenCompraDTO;

@Mapper(componentModel = "spring")
public interface OrdenCompraMapper {

    @Mapping(source = "pedido.id", target = "pedidoId")
    @Mapping(source = "proveedor.id", target = "proveedorId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId")
    OrdenCompraDTO toDTO(OrdenCompra ordenCompra);

    @Mapping(source = "pedidoId", target = "pedido.id")
    @Mapping(source = "proveedorId", target = "proveedor.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id")
    OrdenCompra toEntity(OrdenCompraDTO ordenCompraDTO);

    @Mapping(source = "pedido.id", target = "pedidoId")
    @Mapping(source = "proveedor.id", target = "proveedorId")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "empresa.id", target = "empresaId", ignore = true)
    OrdenCompraDTO toListDTO(OrdenCompra ordenCompra);

    @Mapping(source = "id", target = "id", ignore = true)
    @Mapping(source = "pedidoId", target = "pedido.id")
    @Mapping(source = "proveedorId", target = "proveedor.id")
    @Mapping(source = "estadoId", target = "estado.id")
    @Mapping(source = "empresaId", target = "empresa.id", ignore = true)
    OrdenCompra toRegister(OrdenCompraDTO ordenCompraDTO);

}
