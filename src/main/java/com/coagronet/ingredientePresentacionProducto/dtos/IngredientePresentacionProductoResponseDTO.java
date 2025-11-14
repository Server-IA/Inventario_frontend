package com.coagronet.ingredientePresentacionProducto.dtos;

import lombok.Builder;

@Builder
public record IngredientePresentacionProductoResponseDTO(
                Long idIngredientePresentacionProducto,
                String nombreProducto,
                Long idPresentacionProducto,
                String nombrePresentacionProducto,
                IngredienteDTO ingrediente) {

}
