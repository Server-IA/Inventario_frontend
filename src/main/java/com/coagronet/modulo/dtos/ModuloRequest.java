package com.coagronet.modulo.dtos;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ModuloRequest(
                @NotBlank @Size(max = 100) String nombre,

                @NotBlank @Size(max = 100) String url,

                @Size(max = 2048) String descripcion,

                @Size(max = 255) String icon,

                // --- Relaciones (IDs) ---

                @NotNull @Positive Long estadoId,

                @NotNull @Positive Long subSistemaId,

                @NotNull @Positive Long tipoModuloId,

                @NotNull @Positive Long tipoAplicacionId,

                // --- Listas y otros ---

                // Usamos List en el DTO para facilitar el JSON, luego convertimos a String[]
                List<@NotBlank String> roles,

                @Size(max = 255) String nombreId,

                Boolean requerido

) implements Serializable {
}
