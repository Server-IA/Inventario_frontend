package com.coagronet.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de la asignación (rol y empresa) de un usuario. Una asignación puede ser a nivel de sistema (empresaId = null) o a nivel de empresa.")
public record AsignacionResumenDTO(
                @Schema(description = "ID de la empresa (nulo si el rol es de sistema)", example = "1", nullable = true) Long empresaId,

                @Schema(description = "Nombre de la empresa ('Sin empresa' si la asignación es global)", example = "Tech Solutions") String empresaNombre,

                @Schema(description = "Nombre del rol asignado", example = "Cajero") String rolNombre,

                @Schema(description = "Estado actual de la asignación", example = "Activo") String estadoNombre) {
}