package com.coagronet.articuloKardex.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

}
