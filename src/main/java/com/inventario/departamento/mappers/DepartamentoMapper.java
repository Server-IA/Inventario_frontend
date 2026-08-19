/*=============================================================================
 Nombre del archivo : DepartamentoMapper.java
 Descripcion        : Mapper para conversion entre entidad y DTO de departamentos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2025-03-31 | 1.0.0   | jujcgu               | Creacion del archivo.                                                                                                              |
 | 2026-05-27 | 1.1.0   | JUAN DIAZ            | Refactor de catalogos globales: ajustes en entidades, DTOs, mappers, repositorios y servicios, con validaciones de negocio.        |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.departamento.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.departamento.Departamento;
import com.inventario.departamento.dtos.DepartamentoDTO;

@Mapper(componentModel = "spring")
public interface DepartamentoMapper {

	@Mapping(source = "pais.id", target = "paisId")
	@Mapping(source = "estado.id", target = "estadoId")
	DepartamentoDTO toDTO(Departamento departamento);

	@Mapping(source = "paisId", target = "pais.id")
	@Mapping(source = "estadoId", target = "estado.id")
	Departamento toEntity(DepartamentoDTO departamentoDTO);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "nombre", target = "nombre")
	@Mapping(target = "paisId", source = "pais.id")
	@Mapping(source = "codigo", target = "codigo")
	@Mapping(source = "acronimo", target = "acronimo")
	@Mapping(target = "estadoId", source = "estado.id")
	DepartamentoDTO toListDto(Departamento departamento);

}






