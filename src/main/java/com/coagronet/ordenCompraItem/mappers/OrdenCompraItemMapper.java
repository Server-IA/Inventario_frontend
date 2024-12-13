package com.coagronet.ordenCompraItem.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.ordenCompraItem.OrdenCompraItem;
import com.coagronet.ordenCompraItem.dtos.OrdenCompraItemDTO;

@Mapper(componentModel = "spring")
public interface OrdenCompraItemMapper {

    OrdenCompraItemMapper INSTANCE = Mappers.getMapper(OrdenCompraItemMapper.class);

    @Mapping(source = "ordenCompra.id", target = "ordenCompra")
    @Mapping(source = "productoPresentacion.id", target = "productoPresentacion")
    @Mapping(source = "estado.id", target = "estado")
    OrdenCompraItemDTO toDTO(OrdenCompraItem ordenCompraItem);

    @Mapping(source = "ordenCompra", target = "ordenCompra.id")
    @Mapping(source = "productoPresentacion", target = "productoPresentacion.id")
    @Mapping(source = "estado", target = "estado.id")
    OrdenCompraItem toEntity(OrdenCompraItemDTO ordenCompraItemDTO);

}
