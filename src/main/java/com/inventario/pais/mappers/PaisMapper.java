/*=============================================================================
 Nombre del archivo : PaisMapper.java
 Descripcion        : Mapper para conversion entre entidad y DTO de paises.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2025-03-31 | 1.0.0   | jujcgu               | Creacion del archivo.                                                                                                              |
 | 2026-05-27 | 1.1.0   | JUAN DIAZ            | Refactor de catalogos globales: ajustes en entidades, DTOs, mappers, repositorios y servicios, con validaciones de negocio.        |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.pais.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.inventario.pais.Pais;
import com.inventario.pais.dtos.PaisDTO;

@Mapper(componentModel = "spring")
public interface PaisMapper {

	@Mapping(source = "estado.id", target = "estadoId")
	PaisDTO toDTO(Pais pais);

	@Mapping(source = "estadoId", target = "estado.id")
	Pais toEntity(PaisDTO paisDTO);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "nombre", target = "nombre")
	@Mapping(source = "codigo", target = "codigo")
	@Mapping(source = "acronimo", target = "acronimo")
	@Mapping(target = "estadoId", source = "estado.id")
	PaisDTO toListDto(Pais pais);

}









