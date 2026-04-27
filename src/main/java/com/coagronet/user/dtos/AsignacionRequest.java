package com.coagronet.user.dtos;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;

public record AsignacionRequest(
        @NotNull Long empresaId,
        @NotNull Long rolId,
        @NotNull OffsetDateTime iniciaContratoEn,
        OffsetDateTime finalizaContratoEn) {
}
