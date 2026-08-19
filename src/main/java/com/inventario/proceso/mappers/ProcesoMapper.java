package com.inventario.proceso.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.proceso.Proceso;
import com.inventario.proceso.dtos.ProcesoDTO;

@Mapper(componentModel = "spring")
public interface ProcesoMapper {

	@Mapping(source = "tipoProduccion.id", target = "tipoProduccionId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	ProcesoDTO toDTO(Proceso proceso);

	@Mapping(source = "tipoProduccionId", target = "tipoProduccion.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	Proceso toEntity(ProcesoDTO procesoDTO);

	@Mapping(source = "tipoProduccion.id", target = "tipoProduccionId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	ProcesoDTO toListDTO(Proceso proceso);

}
