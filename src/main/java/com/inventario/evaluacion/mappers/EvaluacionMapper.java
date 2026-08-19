package com.inventario.evaluacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.evaluacion.Evaluacion;
import com.inventario.evaluacion.dtos.EvaluacionDTO;

@Mapper(componentModel = "spring")
public interface EvaluacionMapper {

	@Mapping(source = "tipoEvaluacion.id", target = "tipoEvaluacionId")
	@Mapping(source = "empresa.id", target = "empresaId")
	@Mapping(source = "estado.id", target = "estadoId")
	EvaluacionDTO toDTO(Evaluacion evaluacion);

	@Mapping(source = "tipoEvaluacionId", target = "tipoEvaluacion.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	@Mapping(source = "estadoId", target = "estado.id")
	Evaluacion toEntity(EvaluacionDTO evaluacionDTO);

	@Mapping(source = "tipoEvaluacion.id", target = "tipoEvaluacionId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	@Mapping(source = "estado.id", target = "estadoId")
	EvaluacionDTO toListDTO(Evaluacion evaluacion);

}
