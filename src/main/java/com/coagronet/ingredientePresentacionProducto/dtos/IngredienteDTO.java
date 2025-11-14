package com.coagronet.ingredientePresentacionProducto.dtos;

import java.math.BigDecimal;

public record IngredienteDTO(
        Long idIngrediente,
        String nombreIngrediente,
        BigDecimal cantidad,
        Long idUnidad,
        String nombreUnidad,
        Long idEstado,
        String nombreEstado) {
}
