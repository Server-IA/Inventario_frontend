package com.coagronet.ingredientePresentacionProducto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.coagronet.ingredientePresentacionProducto.IngredientePresentacionProducto;
import com.coagronet.ingredientePresentacionProducto.dtos.IngredientePresentacionProductoDTO;

@Mapper(componentModel = "spring")
public interface IngredientePresentacionProductoMapper {

	@Mapping(source = "ingrediente.id", target = "ingredienteId")
	@Mapping(source = "presentacionProducto.id", target = "presentacionProductoId")
	@Mapping(source = "estado.id", target = "estadoId")
	@Mapping(source = "empresa.id", target = "empresaId", ignore = true)
	IngredientePresentacionProductoDTO toDTO(IngredientePresentacionProducto ingredientePresentacionProducto);

	@Mapping(source = "ingredienteId", target = "ingrediente.id")
	@Mapping(source = "presentacionProductoId", target = "presentacionProducto.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "empresaId", target = "empresa.id")
	IngredientePresentacionProducto toEntity(IngredientePresentacionProductoDTO ingredientePresentacionProductoDTO);

}
