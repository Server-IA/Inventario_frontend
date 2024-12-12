package com.coagronet.pedidoItem.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.pedidoItem.PedidoItem;
import com.coagronet.pedidoItem.dtos.PedidoItemDTO;

@Mapper(componentModel = "spring")
public interface PedidoItemMapper {

    PedidoItemMapper INSTANCE = Mappers.getMapper(PedidoItemMapper.class);

    @Mapping(source = "pedido.id", target = "pedido")
    @Mapping(source = "productoPresentacion.id", target = "productoPresentacion")
    @Mapping(source = "estado.id", target = "estado")
    PedidoItemDTO toDto(PedidoItem pedidoItem);

    @Mapping(source = "pedido", target = "pedido.id")
    @Mapping(source = "productoPresentacion", target = "productoPresentacion.id")
    @Mapping(source = "estado", target = "estado.id")
    PedidoItem toEntity(PedidoItemDTO pedidoItemDTO);

}
