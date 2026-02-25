package com.coagronet.kardex.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.KardexDTO;

@Mapper(componentModel = "spring")
public interface KardexMapper {

	@Mapping(source = "almacen.id", target = "almacenId")
	@Mapping(source = "produccion.id", target = "produccionId")
	@Mapping(source = "tipoMovimiento.id", target = "tipoMovimientoId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	@Mapping(source = "clienteProveedor.id", target = "clienteProveedorId")
	@Mapping(source = "pedido.id", target = "pedidoId")
	@Mapping(source = "ordenCompra.id", target = "ordenCompraId")
	KardexDTO toDto(Kardex kardex);

	@Mapping(target = "almacen", ignore = true)
	@Mapping(target = "produccion", ignore = true)
	@Mapping(target = "tipoMovimiento", ignore = true)
	@Mapping(target = "estado", ignore = true)
	@Mapping(target = "empresa", ignore = true)
	@Mapping(target = "clienteProveedor", ignore = true)
	@Mapping(target = "pedido", ignore = true)
	@Mapping(target = "ordenCompra", ignore = true)
	Kardex toEntity(KardexDTO kardexDTO);

	@org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "empresa", ignore = true)
	@Mapping(target = "clienteProveedor", ignore = true)
	void updateEntityFromDto(KardexDTO dto, @MappingTarget Kardex entity);

}