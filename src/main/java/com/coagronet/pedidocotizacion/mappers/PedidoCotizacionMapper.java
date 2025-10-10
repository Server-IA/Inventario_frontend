package com.coagronet.pedidocotizacion.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.pedidocotizacion.PedidoCotizacion;
import com.coagronet.pedidocotizacion.dtos.PedidoCotizacionRequestDTO;
import com.coagronet.pedidocotizacion.dtos.PedidoCotizacionResponseDTO;

@Mapper(componentModel = "spring")
public interface PedidoCotizacionMapper {

	@Mapping(target = "pedidoId", source = "pedido.id")
	@Mapping(target = "proveedorId", source = "proveedor.id")
	@Mapping(target = "estadoId", source = "estado.id")
	PedidoCotizacionResponseDTO toDTO(PedidoCotizacion pedidoCotizacion);

	@InheritInverseConfiguration(name = "toDTO")
	PedidoCotizacion toEntity(PedidoCotizacionRequestDTO pedidoCotizacionRequestDTO);

}
