package com.inventario.rolpermiso.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar el reemplazo de un permiso individual en un rol-empresa.
 * Caso de uso: "Asigné el permiso X por error, quiero reemplazarlo por Y"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReemplazarPermisoRequest {

    @NotNull(message = "El ID del permiso actual no puede ser nulo")
    private Long permisoIdActual;

    @NotNull(message = "El ID del nuevo permiso no puede ser nulo")
    private Long nuevoPermisoId;
}
