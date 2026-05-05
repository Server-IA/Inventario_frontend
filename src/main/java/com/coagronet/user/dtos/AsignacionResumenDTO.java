package com.coagronet.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de la asignación (rol y empresa) del usuario.")
public record AsignacionResumenDTO(
        @Schema(description = "ID de la empresa", example = "1") Long empresaId,
        @Schema(description = "Nombre de la empresa", example = "Tech Solutions") String empresaNombre,
        @Schema(description = "Nombre del rol", example = "Cajero") String rolNombre,
        @Schema(description = "Estado de la asignación", example = "Activo") String estadoNombre) {
}