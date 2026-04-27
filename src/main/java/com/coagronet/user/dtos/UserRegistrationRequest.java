package com.coagronet.user.dtos;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserRegistrationRequest(
        // Datos de Usuario
        @NotBlank @Email String username,
        @NotBlank String password,

        // Datos de Persona
        @NotNull Long tipoIdentificacionId,
        @NotBlank String identificacion,
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank @Email String emailPersonal,
        String genero,
        LocalDate fechaNacimiento,
        String direccion,
        String celular,

        // Asignaciones
        @NotEmpty @Valid List<AsignacionRequest> asignaciones) {
}
