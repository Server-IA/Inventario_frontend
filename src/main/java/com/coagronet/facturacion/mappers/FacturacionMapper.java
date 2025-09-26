package com.coagronet.facturacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.facturacion.Facturacion;
import com.coagronet.facturacion.dtos.FacturacionDTO;

@Mapper(componentModel = "spring")
public interface FacturacionMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	FacturacionDTO toDTO(Facturacion facturacion);

	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	Facturacion toEntity(FacturacionDTO facturacionDTO);

}
