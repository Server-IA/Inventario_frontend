package com.coagronet.usuariorol.dtos;

import java.time.OffsetDateTime;

public record UsuarioRolResponseDTO(
        Long id,
        Long usuarioId,
        String usuarioEmail,
        Long empresaId,
        String empresaNombre,
        Long rolId,
        String rolNombre,
        Long estadoId,
        String estadoNombre,
        OffsetDateTime iniciaContratoEn,
        OffsetDateTime finalizaContratoEn,
        OffsetDateTime createdAt,
        String createdBy,
        OffsetDateTime updatedAt,
        String updatedBy) {
}