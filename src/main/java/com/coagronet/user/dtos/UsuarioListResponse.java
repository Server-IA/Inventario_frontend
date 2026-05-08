package com.coagronet.user.dtos;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta resumida de un usuario dentro del listado. La lista de asignaciones varía según el rol del solicitante (ver descripción del endpoint).")
public record UsuarioListResponse(
        @Schema(description = "ID único del usuario en la base de datos", example = "1") Long id,

        @Schema(description = "Nombre de usuario (generalmente el correo electrónico)", example = "juan.perez@empresa.com") String username,

        @Schema(description = "Documento de identidad de la persona", example = "1020304050") String identificacion,

        @Schema(description = "Nombres de la persona", example = "Juan") String nombre,

        @Schema(description = "Apellidos de la persona", example = "Pérez") String apellido,

        String celular,

        @Schema(description = "Rol preferido del usuario", example = "ROLE_USUARIO") String rolPreferido,

        @ArraySchema(schema = @Schema(description = "Lista de asignaciones (rol y empresa) visibles para el solicitante. Un administrador de sistema ve todas las asignaciones; otros roles solo ven las de su empresa.")) List<AsignacionResumenDTO> asignaciones) {
}