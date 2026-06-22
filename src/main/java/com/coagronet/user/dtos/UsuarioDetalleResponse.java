/*=============================================================================
 Nombre del archivo : UsuarioDetalleResponse.java
 Descripcion        : DTO de respuesta con el detalle completo de un usuario.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-16 | 0.4.0   | JUAN JOSE CASTRO     | Adición de los atributos    |
 |            |         |                      | estadoId y estadoNombre con |
 |            |         |                      | sus respectivas anotaciones |
 |            |         |                      | @Schema para representar el |
 |            |         |                      | estado del usuario.         |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.user.dtos;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta detallada de un usuario específico.")
public record UsuarioDetalleResponse(
                @Schema(description = "Nombre de usuario (generalmente el correo electrónico)", example = "juan.perez@empresa.com") String username,
                @Schema(description = "ID de la persona", example = "1") Long personaId,
                @Schema(description = "Documento de identidad de la persona", example = "1020304050") String identificacion,
                @Schema(description = "Nombres de la persona", example = "Juan") String nombre,
                @Schema(description = "Apellidos de la persona", example = "Pérez") String apellido,
                @Schema(description = "Género de la persona", example = "Masculino") String genero,
                @Schema(description = "Fecha de nacimiento de la persona", example = "1990-01-01") java.time.LocalDate fechaNacimiento,
                @Schema(description = "Dirección de residencia de la persona", example = "Calle Falsa 123") String direccion,
                @Schema(description = "Número de celular de la persona", example = "3001234567") String celular,
                @Schema(description = "ID del rol preferido del usuario", example = "1", nullable = true) Long rolPreferidoId,
                @Schema(description = "ID de la empresa preferida del usuario", example = "1", nullable = true) Long empresaPreferidaId,
                @Schema(description = "ID del estado del usuario", example = "1") Long estadoId,
                @Schema(description = "Nombre del estado del usuario", example = "Activado") String estadoNombre,
                @ArraySchema(schema = @Schema(description = "Lista de asignaciones")) List<AsignacionResumenAllDTO> asignaciones) {
}
