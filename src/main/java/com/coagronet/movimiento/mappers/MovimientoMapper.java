package com.coagronet.movimiento.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.movimiento.Movimiento;
import com.coagronet.movimiento.dtos.MovimientoDTO;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {
	
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	MovimientoDTO toDTO(Movimiento movimiento);

	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	Movimiento toEntity(MovimientoDTO movimientoDTO);

	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	MovimientoDTO toListDTO(Movimiento movimiento);

}
