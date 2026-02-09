package com.coagronet.modulo.dtos;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ModuloRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres") String nombre,

        @NotBlank(message = "La URL es obligatoria") @Size(max = 100, message = "La URL no puede exceder los 100 caracteres") String url,

        @Size(max = 2048, message = "La descripción no puede exceder los 2048 caracteres") String descripcion,

        @Size(max = 255, message = "El ícono no puede exceder los 255 caracteres") String icon,

        // --- Relaciones (IDs) ---

        @NotNull(message = "El ID del estado es obligatorio") @Positive(message = "El ID del estado debe ser válido") Long estadoId,

        @NotNull(message = "El ID del subsistema es obligatorio") @Positive(message = "El ID del subsistema debe ser válido") Long subSistemaId,

        @NotNull(message = "El ID del tipo de módulo es obligatorio") @Positive(message = "El ID del tipo de módulo debe ser válido") Long tipoModuloId,

        @NotNull(message = "El ID del tipo de aplicación es obligatorio") @Positive(message = "El ID del tipo de aplicación debe ser válido") Long tipoAplicacionId,

        // --- Listas y otros ---

        // Usamos List en el DTO para facilitar el JSON, luego convertimos a String[]
        List<@NotBlank(message = "El rol no puede estar vacío") String> roles,

        @Size(max = 255, message = "El nombre ID no puede exceder los 255 caracteres") String nombreId,

        Boolean requerido

) implements Serializable {
}
