package com.inventario.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Criterios de filtrado para el listado de usuarios. Todos los campos son opcionales; si se omite un campo, no se aplica ese filtro.")
public record UsuarioFiltroRequest(
                @Schema(description = "Filtro por nombre de usuario (correo electrónico)", example = "juan.perez@empresa.com") String username,

                @Schema(description = "Filtro por nombres de la persona (búsqueda parcial, case-insensitive)", example = "Juan") String nombre,

                @Schema(description = "Filtro por apellidos de la persona (búsqueda parcial, case-insensitive)", example = "Pérez") String apellido,

                @Schema(description = "Filtro por ID del rol asignado", example = "5") Long rolId,

                @Schema(description = "Filtro por ID del estado de la asignación", example = "2") Long estadoId,

                @Schema(description = "Filtro por ID de la empresa. Ignorado para ADMINISTRADOR_SISTEMA", example = "10") Long empresaId) {
}
