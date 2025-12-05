package com.coagronet.usuariorol.dtos;

import java.time.OffsetDateTime;

public record UsuarioRolResponseForCurrentEmpresaDTO(
        Long id,
        Long usuarioId,
        String usuarioEmail,
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
