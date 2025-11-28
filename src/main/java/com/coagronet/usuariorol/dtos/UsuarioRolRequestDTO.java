package com.coagronet.usuariorol.dtos;

import java.time.OffsetDateTime;

public record UsuarioRolRequestDTO(
        Long usuarioId,
        Long empresaId,
        Long rolId,
        Long estadoId, 
        OffsetDateTime iniciaContratoEn,
        OffsetDateTime finalizaContratoEn) {
}
