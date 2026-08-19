package com.inventario.espacioOcupacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.espacioOcupacion.EspacioOcupacion;
import com.inventario.espacioOcupacion.dtos.EspacioOcupacionDTO;

@Mapper(componentModel = "spring")
public interface EspacioOcupacionMapper {

	@Mapping(source = "espacio.id", target = "espacioId")
	@Mapping(source = "ocupacion.id", target = "ocupacionId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	EspacioOcupacionDTO toDTO(EspacioOcupacion espacioOcupacion);

	@Mapping(source = "espacioId", target = "espacio.id")
	@Mapping(source = "ocupacionId", target = "ocupacion.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	EspacioOcupacion toEntity(EspacioOcupacionDTO espacioOcupacionDTO);

}
