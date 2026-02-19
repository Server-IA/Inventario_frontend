package com.coagronet.rolpermiso.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para solicitar la asignación de módulos (y todos sus permisos) a un rol.
 * El `rolId` se obtiene desde la URL (`@PathVariable`).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignarModulosPermisoRequest {

    /**
     * Lista de IDs de módulos a asignar.
     * Se asignarán TODOS los permisos de estos módulos.
     */
    @NotEmpty(message = "Debe seleccionar al menos un módulo")
    private List<Long> modulosIds;
}
