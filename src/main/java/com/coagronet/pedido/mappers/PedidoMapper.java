package com.coagronet.pedido.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.coagronet.pedido.Pedido;
import com.coagronet.pedido.dtos.PedidoDTO;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    PedidoMapper INSTANCE = Mappers.getMapper(PedidoMapper.class);

    @Mapping(source = "almacen.id", target = "almacen")
    @Mapping(source = "produccion.id", target = "produccion")
    @Mapping(source = "estado.id", target = "estado")
    PedidoDTO toDto(Pedido pedido);

    @Mapping(source = "almacen", target = "almacen.id")
    @Mapping(source = "produccion", target = "produccion.id")
    @Mapping(source = "estado", target = "estado.id")
    Pedido toEntity(PedidoDTO pedidoDTO);

}
