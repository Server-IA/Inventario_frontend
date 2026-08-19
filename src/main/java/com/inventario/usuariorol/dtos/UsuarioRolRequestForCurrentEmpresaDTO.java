package com.inventario.usuariorol.dtos;

import java.time.OffsetDateTime;

public record UsuarioRolRequestForCurrentEmpresaDTO(
        Long usuarioId,
        Long rolId,
        Long estadoId,
        OffsetDateTime iniciaContratoEn,
        OffsetDateTime finalizaContratoEn) {

}
