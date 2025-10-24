package com.coagronet.tipounidad.mappers;

import com.coagronet.tipounidad.TipoUnidad;
import com.coagronet.tipounidad.dtos.TipoUnidadDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TipoUnidadMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	TipoUnidadDTO toDTO(TipoUnidad unidad);

	@Mapping(source = "estadoId", target = "estado.id")
	TipoUnidad toEntity(TipoUnidadDTO tipoUnidadDTO);

}
