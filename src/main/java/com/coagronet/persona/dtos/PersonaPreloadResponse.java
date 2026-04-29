package com.coagronet.persona.dtos;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto de respuesta con los datos personales para precarga y estado de usuario.")
public record PersonaPreloadResponse(

        @Schema(description = "ID del tipo de identificación.", example = "1") Long tipoIdentificacionId,

        @Schema(description = "Número de documento de identidad.", example = "1020304050") String identificacion,

        @Schema(description = "Nombres de la persona.", example = "Juan") String nombre,

        @Schema(description = "Apellidos de la persona.", example = "Pérez") String apellido,

        @Schema(description = "Correo electrónico personal.", example = "juan.personal@gmail.com") String emailPersonal,

        @Schema(description = "Género de la persona.", example = "M") String genero,

        @Schema(description = "Fecha de nacimiento.", example = "1990-05-15") LocalDate fechaNacimiento,

        @Schema(description = "Dirección de residencia.", example = "Calle 123 # 45-67") String direccion,

        @Schema(description = "Número de teléfono celular.", example = "3001234567") String celular,

        @Schema(description = "Bandera que indica si esta persona ya tiene un usuario de sistema creado.", example = "true") boolean existeUsuario,

        @Schema(description = "Nombre de usuario actual (si existeUsuario es true).", example = "juan.perez@empresa.com") String username) {
}
