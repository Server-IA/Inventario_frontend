package com.coagronet.presentacion.mappers;

import com.coagronet.presentacion.Presentacion;
import com.coagronet.presentacion.dtos.PresentacionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PresentacionMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	PresentacionDTO toDTO(Presentacion presentacion);

	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	Presentacion toEntity(PresentacionDTO presentacionDTO);

}
