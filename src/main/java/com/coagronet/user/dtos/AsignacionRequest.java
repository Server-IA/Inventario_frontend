package com.coagronet.user.dtos;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Detalle de la asignación de un rol dentro de una empresa para el usuario.")
public record AsignacionRequest(

                @Schema(description = "ID de la empresa donde operará el rol. (Ignorado si el usuario en sesión no es Administrador del Sistema).", example = "1") @NotNull Long empresaId,

                @Schema(description = "ID del rol que se le va a asignar. Debe ser un rol activo para la empresa.", example = "5") @NotNull Long rolId,

                @Schema(description = "Fecha y hora exacta en la que inicia la validez de este rol para el usuario.", type = "string", format = "date-time", example = "2026-04-27T08:00:00-05:00") @NotNull OffsetDateTime iniciaContratoEn,

                @Schema(description = "Fecha y hora exacta en la que finaliza la validez de este rol. Debe ser estrictamente posterior a la fecha de inicio.", type = "string", format = "date-time", example = "2027-04-27T08:00:00-05:00") OffsetDateTime finalizaContratoEn) {
}
