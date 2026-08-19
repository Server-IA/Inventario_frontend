package com.inventario.movimiento.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.movimiento.Movimiento;
import com.inventario.movimiento.dtos.MovimientoDTO;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	MovimientoDTO toDTO(Movimiento movimiento);

	@Mapping(source = "estadoId", target = "estado.id")
	Movimiento toEntity(MovimientoDTO movimientoDTO);

}
