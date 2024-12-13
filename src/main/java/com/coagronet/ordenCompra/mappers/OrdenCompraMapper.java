package com.coagronet.ordenCompra.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.ordenCompra.dtos.OrdenCompraDTO;

@Mapper(componentModel = "spring")
public interface OrdenCompraMapper {

    OrdenCompraMapper INSTANCE = Mappers.getMapper(OrdenCompraMapper.class);

    @Mapping(source = "pedido.id", target = "pedido")
    @Mapping(source = "proveedor.id", target = "proveedor")
    @Mapping(source = "estado.id", target = "estado")
    OrdenCompraDTO toDTO(OrdenCompra ordenCompra);

    @Mapping(source = "pedido", target = "pedido.id")
    @Mapping(source = "proveedor", target = "proveedor.id")
    @Mapping(source = "estado", target = "estado.id")
    OrdenCompra toEntity(OrdenCompraDTO ordenCompraDTO);

}
