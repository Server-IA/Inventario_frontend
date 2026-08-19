package com.inventario.rolpermiso.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar el reemplazo de todos los permisos de un módulo por otro.
 * Caso de uso: "Asigné el módulo X por error, quiero reemplazarlo completamente por el módulo Y"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReemplazarModuloRequest {

    @NotNull(message = "El ID del módulo actual no puede ser nulo")
    private Long moduloIdActual;

    @NotNull(message = "El ID del nuevo módulo no puede ser nulo")
    private Long nuevoModuloId;
}
