package com.coagronet.persona.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PersonaDTO {
    private Long id;

    @NotNull(message = "El tipo de identificación es obligatorio")
    private Integer tipoIdentificacion;

    @NotBlank(message = "La identificación es obligatoria")
    @Size(max = 255, message = "La identificación no debe superar los 255 caracteres.")
    private String identificacion;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 70, message = "El nombre no debe superar los 70 caracteres.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 70, message = "El apellido no debe superar los 70 caracteres.")
    private String apellido;

    @Size(max = 10, message = "El genero no debe superar los 10 caracteres.")
    private String genero;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    @Min(1)
    @Max(10)
    private Long estrato;

    @Size(max = 255, message = "La dirección no debe superar los 255 caracteres")
    private String direccion;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un email válido")
    private String email;

    @Size(max = 30, message = "El numero de celular no debe superar los 30 caracteres")
    private String celular;

    @NotNull(message = "El estado es obligatorio")
    private Long estado;
}
