package com.coagronet.ocupacion.mappers;

import com.coagronet.ocupacion.Ocupacion;
import com.coagronet.ocupacion.dtos.OcupacionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OcupacionMapper {

	@Mapping(source = "tipoActividad.id", target = "tipoActividadId")
	@Mapping(source = "evaluacion.id", target = "evaluacionId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	OcupacionDTO toDTO(Ocupacion ocupacion);

	@Mapping(source = "tipoActividadId", target = "tipoActividad.id")
	@Mapping(source = "evaluacionId", target = "evaluacion.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	Ocupacion toEntity(OcupacionDTO ocupacionDTO);

	@Mapping(source = "tipoActividad.id", target = "tipoActividadId")
	@Mapping(source = "evaluacion.id", target = "evaluacionId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	OcupacionDTO toListDTO(Ocupacion ocupacion);

}
