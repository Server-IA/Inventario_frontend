/*=============================================================================
 Nombre del archivo : ArticuloKardexMapper.java
 Descripcion        : Mapper para la conversión entre ArticuloKardex y DTOs.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-21 | 0.4.0   | JUAN JOSE CASTRO     | Eliminación de la anotación |
 |            |         |                      | @Mapping que ignoraba el    |
 |            |         |                      | campo fechaHora en el       |
 |            |         |                      | método updateEntityFromDto. |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.articuloKardex.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.dtos.ArticuloKardexDTO;

@Mapper(componentModel = "spring")
public interface ArticuloKardexMapper {

	@Mapping(source = "kardex.id", target = "kardexId")
	@Mapping(source = "presentacionProducto.id", target = "presentacionProductoId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId")
	ArticuloKardexDTO toDTO(ArticuloKardex articuloKardex);

	@Mapping(source = "kardexId", target = "kardex.id")
	@Mapping(source = "presentacionProductoId", target = "presentacionProducto.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	ArticuloKardex toEntity(ArticuloKardexDTO articuloKardexDTO);

	@Mapping(source = "kardex.id", target = "kardexId")
	@Mapping(source = "presentacionProducto.id", target = "presentacionProductoId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	ArticuloKardexDTO toListDTO(ArticuloKardex articuloKardex);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "kardex", ignore = true)
	@Mapping(target = "presentacionProducto", ignore = true)
	@Mapping(target = "estado", ignore = true)
	@Mapping(target = "empresa", ignore = true)
	void updateEntityFromDto(ArticuloKardexDTO dto, @MappingTarget ArticuloKardex entity);

}
