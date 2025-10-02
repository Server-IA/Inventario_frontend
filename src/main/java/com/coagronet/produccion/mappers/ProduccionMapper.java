package com.coagronet.produccion.mappers;

import com.coagronet.produccion.dtos.ProduccionCreateDTO;
import com.coagronet.produccion.dtos.ProduccionDTO;
import org.mapstruct.*;

import com.coagronet.produccion.Produccion;

@Mapper(componentModel = "spring")
public interface ProduccionMapper {

	@Mapping(source = "tipoProduccion.id", target = "tipoProduccionId")
	@Mapping(source = "espacio.id", target = "espacioId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "subSeccion.id", target = "subSeccionId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	ProduccionDTO toDto(Produccion produccion);

	@Mapping(source = "tipoProduccionId", target = "tipoProduccion.id")
	@Mapping(source = "espacioId", target = "espacio.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "subSeccionId", target = "subSeccion.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	Produccion toEntity(ProduccionDTO produccionDTO);


	//registro
	@Mapping(source = "tipoProduccionId", target = "tipoProduccion.id")
	@Mapping(source = "espacioId", target = "espacio.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "subSeccionId", target = "subSeccion.id")
	Produccion toEntity(ProduccionCreateDTO produccionDTO);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromDto(ProduccionDTO dto, @MappingTarget Produccion produccion);

}
