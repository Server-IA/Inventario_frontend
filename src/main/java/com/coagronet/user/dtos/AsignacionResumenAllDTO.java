package com.coagronet.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de la asignación de un usuario sin filtros.")
public record AsignacionResumenAllDTO(
        @Schema(description = "ID de la relación usuario-rol", example = "1") Long usuarioRolId,
        @Schema(description = "ID de la empresa (nulo si el rol es de sistema)", example = "1", nullable = true) Long empresaId,
        @Schema(description = "ID del rol asignado", example = "2") Long rolId,
        @Schema(description = "ID del estado actual de la asignación", example = "1") Long estadoId,
        @Schema(description = "Fecha de inicio del contrato", example = "2023-01-01T00:00:00Z") java.time.OffsetDateTime fechaInicioContrato,
        @Schema(description = "Fecha de fin del contrato", example = "2024-01-01T00:00:00Z", nullable = true) java.time.OffsetDateTime fechaFinContrato) {
}
