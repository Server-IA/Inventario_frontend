package com.inventario.facturacion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.facturacion.Facturacion;
import com.inventario.facturacion.dtos.FacturacionDTO;

@Mapper(componentModel = "spring")
public interface FacturacionMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	FacturacionDTO toDTO(Facturacion facturacion);

	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	Facturacion toEntity(FacturacionDTO facturacionDTO);

}
