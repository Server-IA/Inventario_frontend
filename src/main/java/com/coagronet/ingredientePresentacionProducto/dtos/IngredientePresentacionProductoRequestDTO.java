package com.coagronet.ingredientePresentacionProducto.dtos;

import jakarta.validation.constraints.NotNull;

public record IngredientePresentacionProductoRequestDTO(
                @NotNull(message = "{validation.ingrediente.not-null}") Long ingredienteId,
                @NotNull(message = "{validation.presentacion-producto.not-null}") Long presentacionProductoId,
                @NotNull(message = "{validation.estado.not-null}") Long estadoId,
                Integer cantidad,
                @NotNull(message = "{validation.unidad.not-null}") Long unidadId) {
}
