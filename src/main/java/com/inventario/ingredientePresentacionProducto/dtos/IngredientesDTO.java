package com.inventario.ingredientePresentacionProducto.dtos;

import java.math.BigDecimal;

public record IngredientesDTO(
                Long idIngrediente,
                String nombreIngrediente,
                BigDecimal cantidad,
                Long idUnidad,
                String nombreUnidad,
                Long idEstado,
                String nombreEstado) {
}
