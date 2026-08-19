package com.inventario.tipoIdentificacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.tipoIdentificacion.TipoIdentificacion;
import com.inventario.tipoIdentificacion.dtos.TipoIdentificacionDTO;

@Mapper(componentModel = "spring")
public interface TipoIdentificacionMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	TipoIdentificacionDTO toDTO(TipoIdentificacion tipoIdentificacion);

	@Mapping(source = "estadoId", target = "estado.id")
	TipoIdentificacion toEntity(TipoIdentificacionDTO tipoIdentificacionDTO);

}
