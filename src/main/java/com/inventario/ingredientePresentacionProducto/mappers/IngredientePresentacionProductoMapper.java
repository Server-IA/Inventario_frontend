package com.inventario.ingredientePresentacionProducto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.inventario.ingredientePresentacionProducto.IngredientePresentacionProducto;
import com.inventario.ingredientePresentacionProducto.dtos.IngredientePresentacionProductoRequestDTO;
import com.inventario.ingredientePresentacionProducto.dtos.IngredientePresentacionProductoResponseDTO;

@Mapper(componentModel = "spring")
public interface IngredientePresentacionProductoMapper {

	@Mapping(target = "idIngredientePresentacionProducto", source = "id")
	@Mapping(target = "nombreProducto", source = "presentacionProducto.producto.nombre")
	@Mapping(target = "idPresentacionProducto", source = "presentacionProducto.id")
	@Mapping(target = "nombrePresentacionProducto", source = "presentacionProducto.nombre")
	@Mapping(target = "ingrediente", expression = "java(new com.inventario.ingredientePresentacionProducto.dtos.IngredientesDTO("
			+
			"entity.getIngrediente().getId(), " +
			"entity.getIngrediente().getNombre(), " +
			"entity.getCantidad(), " +
			"(entity.getUnidad() != null ? entity.getUnidad().getId() : null), " +
			"(entity.getUnidad() != null ? entity.getUnidad().getNombre() : null), " +
			"entity.getEstado().getId(), " +
			"entity.getEstado().getNombre()" +
			"))")
	IngredientePresentacionProductoResponseDTO toDto(IngredientePresentacionProducto entity);

	default Page<IngredientePresentacionProductoResponseDTO> toPage(Page<IngredientePresentacionProducto> page) {
		return page.map(this::toDto);
	}

	@Mapping(source = "ingredienteId", target = "ingrediente.id")
	@Mapping(source = "presentacionProductoId", target = "presentacionProducto.id")
	@Mapping(source = "estadoId", target = "estado.id")
	@Mapping(source = "unidadId", target = "unidad.id")
	@Mapping(target = "empresa", ignore = true)
	@Mapping(target = "id", ignore = true)
	IngredientePresentacionProducto toEntity(
			IngredientePresentacionProductoRequestDTO ingredientePresentacionProductoRequestDTO);

}
