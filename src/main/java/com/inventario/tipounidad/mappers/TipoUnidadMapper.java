package com.inventario.tipounidad.mappers;

import com.inventario.tipounidad.TipoUnidad;
import com.inventario.tipounidad.dtos.TipoUnidadDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TipoUnidadMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	TipoUnidadDTO toDTO(TipoUnidad unidad);

	@Mapping(source = "estadoId", target = "estado.id")
	TipoUnidad toEntity(TipoUnidadDTO tipoUnidadDTO);

}
