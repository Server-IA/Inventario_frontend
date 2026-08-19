/*=============================================================================
 Nombre del archivo : UserRegistrationRequest.java
 Descripcion        : Objeto de petición para el registro de un nuevo usuario,
                      información personal y asignación de roles.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-16 | 0.4.0   | JUAN JOSE CASTRO     | Eliminación del campo       |
 |            |         |                      | password de la petición y   |
 |            |         |                      | ajuste de indentación en    |
 |            |         |                      | los demás atributos.        |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.inventario.user.dtos;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Objeto de petición para el registro de un nuevo usuario, información personal y asignación de roles empresariales.")
public record UserRegistrationRequest(

        @Schema(description = "Correo electrónico que servirá como nombre de usuario para el inicio de sesión.", example = "juan.perez@empresa.com") @NotBlank @Email String username,

        @Schema(description = "ID del tipo de identificación (ej. Cédula, Pasaporte).", example = "1") @NotNull Long tipoIdentificacionId,

        @Schema(description = "Número de documento de identidad de la persona.", example = "1020304050") @NotBlank String identificacion,

        @Schema(description = "Nombres de la persona.", example = "Juan") @NotBlank String nombre,

        @Schema(description = "Apellidos de la persona.", example = "Pérez") @NotBlank String apellido,

        @Schema(description = "Correo electrónico personal de contacto.", example = "juan.personal@gmail.com") @NotBlank @Email String emailPersonal,

        @Schema(description = "Género de la persona (ej. M, F, O).", example = "M") String genero,

        @Schema(description = "Fecha de nacimiento de la persona.", example = "1990-05-15") LocalDate fechaNacimiento,

        @Schema(description = "Dirección de residencia.", example = "Calle 123 # 45-67") String direccion,

        @Schema(description = "Número de teléfono celular.", example = "3001234567") String celular,

        @Schema(description = "Estrato socioeconómico de la vivienda (1-6).", example = "3") @NotNull Integer estrato,

        @Schema(description = "Lista de roles y empresas que se le asignarán al usuario. Debe contener al menos una asignación.") @NotEmpty @Valid List<AsignacionRequest> asignaciones) {
}
