package com.coagronet.user.dtos;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto de petición para actualizar la información de un usuario, su persona y sus asignaciones.")
public record UsuarioUpdateRequest(
        @Schema(description = "Nombre de usuario (generalmente el correo electrónico)", example = "juan.perez@empresa.com") @NotBlank String username,
        @Schema(description = "ID del tipo de identificación (ej. Cédula, Pasaporte).", example = "1") Long tipoIdentificacionId,
        @Schema(description = "Documento de identidad de la persona", example = "1020304050") @NotBlank String identificacion,
        @Schema(description = "Nombres de la persona", example = "Juan") @NotBlank String nombre,
        @Schema(description = "Apellidos de la persona", example = "Pérez") @NotBlank String apellido,
        @Schema(description = "Correo electrónico personal de contacto.", example = "juan.personal@gmail.com") String emailPersonal,
        @Schema(description = "Género de la persona", example = "Masculino") String genero,
        @Schema(description = "Fecha de nacimiento de la persona", example = "1990-01-01") LocalDate fechaNacimiento,
        @Schema(description = "Dirección de residencia de la persona", example = "Calle Falsa 123") String direccion,
        @Schema(description = "Número de celular de la persona", example = "3001234567") String celular,
        @Schema(description = "Estrato socioeconómico de la vivienda (1-6).", example = "3") Integer estrato,
        @Schema(description = "ID del rol preferido del usuario", example = "1", nullable = true) Long rolPreferidoId,
        @Schema(description = "ID de la empresa preferida del usuario", example = "1", nullable = true) Long empresaPreferidaId,
        @ArraySchema(schema = @Schema(description = "Lista de asignaciones")) @Valid List<AsignacionUpdateRequest> asignaciones) {
}
