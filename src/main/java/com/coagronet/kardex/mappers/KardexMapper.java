package com.coagronet.kardex.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.KardexDTO;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface KardexMapper {

	@Mapping(source = "almacen.id", target = "almacenId")
	@Mapping(source = "produccion.id", target = "produccionId")
	@Mapping(source = "tipoMovimiento.id", target = "tipoMovimientoId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	@Mapping(source = "clienteProveedor.id", target = "clienteProveedorId")
	KardexDTO toDto(Kardex kardex);

	@Mapping(source = "almacenId", target = "almacen.id")
	@Mapping(source = "produccionId", target = "produccion.id")
	@Mapping(source = "tipoMovimientoId", target = "tipoMovimiento.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(target = "clienteProveedor", ignore = true)
	@Mapping(source = "empresaId", target = "empresa.id")
	Kardex toEntity(KardexDTO kardexDTO);

	@org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "empresa", ignore = true)
	@Mapping(target = "clienteProveedor", ignore = true)
	void updateEntityFromDto(KardexDTO dto, @MappingTarget Kardex entity);
}