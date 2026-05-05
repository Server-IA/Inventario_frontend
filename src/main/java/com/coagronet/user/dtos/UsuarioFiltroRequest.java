package com.coagronet.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Criterios de filtrado para el listado de usuarios.")
public record UsuarioFiltroRequest(
        String username,
        String nombre,
        String apellido,
        Long rolId,
        Long estadoId,
        Long empresaId) {
}
