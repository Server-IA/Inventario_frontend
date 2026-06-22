/*=============================================================================
 Nombre del archivo : UsuarioListResponse.java
 Descripcion        : DTO de respuesta para el listado resumido de usuarios.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-16 | 0.4.0   | JUAN JOSE CASTRO     | Adición de los atributos    |
 |            |         |                      | estadoId y estadoNombre con |
 |            |         |                      | sus respectivas anotaciones |
 |            |         |                      | @Schema para incluir el     |
 |            |         |                      | estado en el listado.       |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

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

        @Schema(description = "Género de la persona", example = "Masculino") String genero,

        @Schema(description = "Fecha de nacimiento de la persona", example = "1990-01-01") java.time.LocalDate fechaNacimiento,

        @Schema(description = "Dirección de residencia de la persona", example = "Calle Falsa 123") String direccion,

        @Schema(description = "Número de celular de la persona", example = "3001234567") String celular,

        @Schema(description = "Rol preferido del usuario", example = "ROLE_USUARIO") String rolPreferido,

        @Schema(description = "ID del estado del usuario", example = "1") Long estadoId,

        @Schema(description = "Nombre descriptivo del estado del usuario", example = "Pendiente verificación") String estadoNombre,

        @ArraySchema(schema = @Schema(description = "Lista de asignaciones (rol y empresa) visibles para el solicitante. Un administrador de sistema ve todas las asignaciones; otros roles solo ven las de su empresa.")) List<AsignacionResumenDTO> asignaciones) {
}