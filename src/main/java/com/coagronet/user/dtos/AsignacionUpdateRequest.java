package com.coagronet.user.dtos;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Detalle de la asignación de un rol para la actualización de un usuario.")
public record AsignacionUpdateRequest(
                @Schema(description = "ID de la relación usuario-rol. Si es nulo, indica que se debe crear una nueva asignación.", example = "1", nullable = true) Long usuarioRolId,
                @Schema(description = "ID de la empresa (solo aplicable para nuevas asignaciones).", example = "1", nullable = true) Long empresaId,
                @Schema(description = "ID del rol asignado.", example = "2") @NotNull Long rolId,
                @Schema(description = "ID del estado actual de la asignación.", example = "1") @NotNull Long estadoId,
                @Schema(description = "Fecha de inicio del contrato.", example = "2023-01-01T00:00:00Z") @NotNull OffsetDateTime fechaInicioContrato,
                @Schema(description = "Fecha de fin del contrato.", example = "2024-01-01T00:00:00Z", nullable = true) OffsetDateTime fechaFinContrato) {

        @AssertTrue(message = "La fecha de finalización debe ser estrictamente posterior a la fecha de inicio")
        public boolean isFechaFinalizacionValida() {
                if (fechaInicioContrato == null || fechaFinContrato == null) {
                        return true;
                }
                return fechaFinContrato.isAfter(fechaInicioContrato);
        }
}
