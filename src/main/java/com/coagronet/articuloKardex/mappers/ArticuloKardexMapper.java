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
	@Mapping(source = "username.username", target = "username")
	ArticuloKardexDTO toDTO(ArticuloKardex articuloKardex);

	@Mapping(source = "kardexId", target = "kardex.id")
	@Mapping(source = "presentacionProductoId", target = "presentacionProducto.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	@Mapping(target = "username", ignore = true)
	@Mapping(target = "fechaHora", ignore = true)
	ArticuloKardex toEntity(ArticuloKardexDTO articuloKardexDTO);

	@Mapping(source = "kardex.id", target = "kardexId")
	@Mapping(source = "presentacionProducto.id", target = "presentacionProductoId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	@Mapping(source = "username.username", target = "username")
	ArticuloKardexDTO toListDTO(ArticuloKardex articuloKardex);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "kardex", ignore = true)
	@Mapping(target = "presentacionProducto", ignore = true)
	@Mapping(target = "estado", ignore = true)
	@Mapping(target = "empresa", ignore = true)
	@Mapping(target = "fechaHora", ignore = true)
	@Mapping(target = "username", ignore = true)
	void updateEntityFromDto(ArticuloKardexDTO dto, @MappingTarget ArticuloKardex entity);

}
