package com.coagronet.user.dtos;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta resumida de un usuario para el listado.")
public record UsuarioListResponse(
        @Schema(description = "ID del usuario", example = "1") Long id,
        @Schema(description = "Nombre de usuario", example = "juan.perez@empresa.com") String username,
        @Schema(description = "Documento de identidad", example = "1020304050") String identificacion,
        @Schema(description = "Nombres de la persona", example = "Juan") String nombre,
        @Schema(description = "Apellidos de la persona", example = "Pérez") String apellido,
        @Schema(description = "Lista de roles y empresas asignadas al usuario") List<AsignacionResumenDTO> asignaciones) {
}
