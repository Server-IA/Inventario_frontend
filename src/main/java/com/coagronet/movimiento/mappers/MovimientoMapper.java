package com.coagronet.movimiento.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.movimiento.Movimiento;
import com.coagronet.movimiento.dtos.MovimientoDTO;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	MovimientoDTO toDTO(Movimiento movimiento);

	@Mapping(source = "estadoId", target = "estado.id")
	Movimiento toEntity(MovimientoDTO movimientoDTO);

}
