/*=============================================================================
 Nombre del archivo : MunicipioMapper.java
 Descripcion        : Mapper para conversion entre entidad y DTO de municipios.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2025-03-31 | 1.0.0   | jujcgu               | Creacion del archivo.                                                                                                              |
 | 2026-05-27 | 1.1.0   | JUAN DIAZ            | Refactor de catalogos globales: ajustes en entidades, DTOs, mappers, repositorios y servicios, con validaciones de negocio.        |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.municipio.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.inventario.municipio.Municipio;
import com.inventario.municipio.dtos.MunicipioDTO;

@Mapper(componentModel = "spring")
public interface MunicipioMapper {

	@Mapping(source = "departamento.id", target = "departamentoId")
	@Mapping(source = "estado.id", target = "estadoId")
	MunicipioDTO toDTO(Municipio municipio);

	@Mapping(source = "departamentoId", target = "departamento.id")
	@Mapping(source = "estadoId", target = "estado.id")
	Municipio toEntity(MunicipioDTO municipioDTO);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "nombre", source = "nombre")
	@Mapping(target = "departamentoId", source = "departamento.id")
	@Mapping(target = "codigo", source = "codigo")
	@Mapping(target = "acronimo", source = "acronimo")
	@Mapping(target = "estadoId", source = "estado.id")
	MunicipioDTO toListDto(Municipio municipio);

}









