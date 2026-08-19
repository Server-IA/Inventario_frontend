package com.inventario.empresa.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.inventario.empresa.Empresa;
import com.inventario.empresa.dtos.EmpresaDTO;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

	EmpresaMapper INSTANCE = Mappers.getMapper(EmpresaMapper.class);

	@Mapping(source = "tipoIdentificacion.id", target = "tipoIdentificacionId")
	@Mapping(source = "persona.id", target = "personaId")
	@Mapping(source = "estado.id", target = "estadoId")
	EmpresaDTO toEmpresaDTO(Empresa empresa);

	@Mapping(source = "tipoIdentificacionId", target = "tipoIdentificacion.id")
	@Mapping(source = "personaId", target = "persona.id")
	@Mapping(source = "estadoId", target = "estado.id")
	Empresa toEmpresa(EmpresaDTO empresaDTO);

}
