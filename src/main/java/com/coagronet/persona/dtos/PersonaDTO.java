package com.coagronet.persona.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PersonaDTO {
    private Long id;
    private Integer tipoIdentificacion;
    private String identificacion;
    private String nombre;
    private String apellido;
    private String genero;
    private LocalDate fechaNacimiento;
    private Integer estrato;
    private String direccion;
    private String email;
    private String celular;
    private Integer estado;
}
